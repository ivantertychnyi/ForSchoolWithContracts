package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;

public class EventMediator implements EventMed {

	@Inject
	private CrudServiceDAO dao;

	@Inject
	private DateFilter dateFilter;

	private Event unit;

	public Event getUnit() {
		return unit;
	}

	public void setUnit(Event unit) {
		this.unit = unit;
	}

	public Date getDate() {
		try {
			return unit.getDate();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Teacher getTeacher() {
		try {
			return dao.find(Teacher.class, unit.getHostId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public List<Client> getClients() {
		try {
			List<Client> clients = new ArrayList<Client>();
			for (Contract c : getContracts())
				clients.add(c.getClient());
			return clients;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public EventType getEventType() {
		try {
			return unit.getEventType();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public List<Contract> getContracts() {
		try {
			return unit.getContracts();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getPrice() {
		return unit.getEventType().getMoney();
	}

	public EventState getState() {
		// TODO activate event state
		return null;
	}

	public String getComment() {
		try {
			return unit.getComment();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Facility getFacility() {
		try {
			return dao.find(Facility.class, unit.getFacilityId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Room getRoom() {
		try {
			return dao.find(Room.class, unit.getRoomId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	private Map<String, Object> appliedFilters;
	private List<Event> cache;

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	public List<Event> getGroup() {
		if (cache == null)
			cache = dao.findWithNamedQuery(Event.ALL);
		return cache;
	}

	public void setGroup(List<Event> items) {
		cache = items;
	}

	public List<Event> getAllEvents() {
		return dao.findWithNamedQuery(Event.ALL);
	}

	public void reset() {
		cache = null;
		appliedFilters = null;
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public EventMed filter(Teacher unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BY_TEACHER_ID, QueryParameters
					.with("teacherId", unit.getId()).parameters());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			if (cache.get(i).getHostId() != unit.getId())
				cache.remove(i);
		return this;
	}

	public EventMed filter(Contract unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			setGroup(unit.getEvents());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			evnt: {
				for (Contract c : cache.get(i).getContracts()) {
					if (c.getId() == unit.getId())
						break evnt;
					cache.remove(i);
				}
			}
		return this;
	}

	public EventMed filter(EventState state) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BY_STATE, QueryParameters
					.with("state", state).parameters());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			if (cache.get(i).getState() != state)
				cache.remove(i);
		return this;
	}

	public EventMed filter(Facility unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BY_FACILITY_ID,
					QueryParameters.with("facilityId", unit.getFacilityId())
							.parameters());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			if (cache.get(i).getFacilityId() != unit.getFacilityId())
				cache.remove(i);
		return this;
	}

	public EventMed filter(Room unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BY_ROOM_ID, QueryParameters
					.with("roomId", unit.getRoomId()).parameters());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			if (cache.get(i).getRoomId() != unit.getRoomId())
				cache.remove(i);
		return this;
	}

	public EventMed filter(EventType type) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BY_TYPE_ID, QueryParameters
					.with("typeId", type.getId()).parameters());
			return this;
		}

		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--)
			if (cache.get(i).getTypeId() != type.getId())
				cache.remove(i);
		return this;
	}

	public EventMed filter(Date date1, Date date2) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Event.BETWEEN_DATE1_AND_DATE2,
					QueryParameters.with("date1", date1).and("date2", date2)
							.parameters());
			return this;
		}

		dateFilter.filter(getGroup(), date1, date2);

		return this;
	}

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Integer count(EventState state) {
		return filter(state).countGroupSize();
	}

	public Integer countEventsComplete() {
		return count(EventState.complete);
	}

	public Integer countEventsFailed() {
		return count(EventState.failed) + count(EventState.failedByClient)
				+ count(EventState.failedByTeacher);
	}

	public Integer countEventsFailedByClient() {
		return count(EventState.failedByClient);
	}

	public Integer countEventsFailedByTeacher() {
		return count(EventState.failedByTeacher);
	}

	public Integer countMoney() {
		if(cache==null) return null;
		int summ = 0;
		for(Event e : getGroup())
			summ+=e.getMoney();
		return summ;
	}

	public Integer countMoneyOfCompleteEvents() {
		return filter(EventState.complete).countMoney();
	}

	public Integer countMoneyOfFailedEvents() {
		return filter(EventState.complete).countMoney() + countMoneyOfEventsFailedByClient() + countMoneyOfEventsFailedByTeacher();
	}

	public Integer countMoneyOfEventsFailedByClient() {
		return filter(EventState.failedByClient).countMoney();
	}

	public Integer countMoneyOfEventsFailedByTeacher() {
		return filter(EventState.failedByTeacher).countMoney();
	}

	public Integer countGivenPercentOfMoney(int percent) {
		return countMoney()*100/percent;
	}

	public int countDaysInEventsGroup() {
		
	}

}