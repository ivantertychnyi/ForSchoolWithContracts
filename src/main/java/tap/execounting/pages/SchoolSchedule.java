package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.FacilitySelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;

public class SchoolSchedule {

	//code helpers
	@Inject
	private CrudServiceDAO dao;
	@Inject
	private Request request;
	@Inject
	private SuperCalendar sc;

	//page components
	@Component
	private Zone editorZone;
	@Component(id="resultszone")
	private Zone resultsZone;
	@Component(id="datelabelzone")
	private Zone datelabelZone;
	@Component
	private AddEvent eventEditor;
	@SuppressWarnings("unused")
	@Property
	private FacilitySelectModel facilityselect;
	
	//page properties
	@Property
	@Persist
	private Facility facility;
	@Property
	@Persist
	private Date currentDate;
	@Property
	private int row;
	@Persist
	private int rows;
	@Property
	@Persist
	private int facilityId;
	@Property
	private Room room;
	@SuppressWarnings("unused")
	@Property
	private boolean adding;
	private Event[][] eventsArray;

	
	public List<Integer> getRows() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < rows; i++)
			list.add(i + 1);
		return list;
	}

	public String getFormattedDate() {
		sc.setTime(currentDate);
		return sc.dateString();
	}

	public Event getEvent() {
		int idx = 0;
		for (int i = 0; i < facility.getRoomsNumber(); i++)
			if (facility.getRooms().get(i).getRoomId() == room.getRoomId())
				idx = i;
		System.out.println("idx: " + idx + " row: " + row);
		return eventsArray[idx][row-1];
	}

	void setup(Facility f) {
		this.facility = f;
		currentDate = new Date();
		facilityId = f.getFacilityId();
	}

	@SetupRender
	void onPrepareForRender() {
		facilityselect = new FacilitySelectModel(dao);
		refreshEvents();
	}
	
	Object onSuccessFromControlPanel(){
		refreshEvents();
		facility = dao.find(Facility.class, facilityId);
		return request.isXHR() ? new MultiZoneUpdate(resultsZone).add(datelabelZone) : null; 
	}
	
	Object onAdd(){
		adding = true;
		eventEditor.setup(facility);
		return request.isXHR() ? editorZone.getBody() : null;
	}

	private void refreshEvents() {
		
		System.out.println("\n\nRefreshing\n\n");
		@SuppressWarnings("unchecked")
		List<Event>[] pan = new List[facility.getRoomsNumber()];
		List<Room> rooms = facility.getRooms();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("date", currentDate);

		rows = 0;
		for (int i = 0; i < rooms.size(); i++) {
			Room r = rooms.get(i);
			params.put("roomId", r.getRoomId());
			pan[i] = dao.findWithNamedQuery(Event.BY_ROOM_ID_AND_DATE, params);
			String s = "room: " + r.getRoomId() + "\tevents: " + pan[i].size();
			System.out.println("params: " + params.get("roomId") + " " + params.get("date"));
			System.out.println("\n" + s + "\n");
			sort(pan[i]);

			if (pan[i].size() > rows)
				rows = pan[i].size();
		}

		eventsArray = new Event[rooms.size()][];
		for (int i = 0; i < rooms.size(); i++) {
			eventsArray[i] = new Event[rows];
			Iterator<Event> e = pan[i].iterator();
			int j = 0;
			while (e.hasNext()) {
				eventsArray[i][j] = e.next();
				j++;
			}
		}
		
		System.out.println("\n\nfinished refreshing\n\n");
	}

	private void sort(List<Event> list) {
		for (int i = 0; i < list.size(); i++)
			for (int j = list.size() - 1; j >= 0; j--) {
				Event ei = list.get(i);
				Event ej = list.get(j);
				if (ei.getDate().before(ej.getDate())) {
					list.set(i, ej);
					list.set(j, ei);
				}
			}
	}
}