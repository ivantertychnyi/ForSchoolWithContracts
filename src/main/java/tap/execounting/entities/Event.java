package tap.execounting.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.tapestry5.beaneditor.Validate;

@Entity
@NamedQueries({
		@NamedQuery(name = Event.ALL, query = "Select se from Event se"),
		@NamedQuery(name = Event.BY_FACILITY_ID, query = "Select se from Event se where se.facilityId = :facilityId"),
		@NamedQuery(name = Event.BY_TEACHER_ID, query = "Select se from Event se where se.hostId = :teacherId"),
		@NamedQuery(name = Event.BY_TEACHER_ID_AND_DATE, query = "Select se from Event se "
				+ "where se.hostId = :teacherId "
				+ "and se.date between :earlierDate and :laterDate"),
		@NamedQuery(name = Event.BY_ROOM_ID_AND_DATE, query = "Select e from Event e where e.roomId = :roomId "
				+ "and date(e.date) = date(:date)"),
		@NamedQuery(name = Event.BY_TYPE_ID, query = "Select e from Event e where e.typeId = :typeId") })
@Table(name = "events")
public class Event implements Comparable<Event> {

	public static final String ALL = "Event.all";
	public static final String BY_FACILITY_ID = "Event.byFacilityId";
	public static final String BY_ROOM_ID_AND_DATE = "Event.byRoomIdAndDate";
	public static final String BY_TEACHER_ID = "Event.byTeacherId";
	public static final String BY_TEACHER_ID_AND_DATE = "Event.byTeacherIdAndDate";
	public static final String BY_TYPE_ID = "Event.byTypeId";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private int id;

	@Validate("required")
	@NotNull
	@Column(nullable = false, name = "host_id")
	private int hostId;

	@NotNull
	@Column(nullable = false, name = "facility_id")
	private int facilityId;

	@Column(name = "room_id")
	private int roomId;

	@Column(name = "date")
	private Date date;

	private boolean state;

	@Column(name = "type_id")
	private int typeId;

	@OneToOne(optional = false)
	@JoinColumn(name = "type_id", nullable = false, updatable = false, insertable = false)
	private EventType eventType;

	private String comment;

	@OneToMany
	@JoinTable(name = "events_contracts", joinColumns = { @JoinColumn(name = "event_id") }, inverseJoinColumns = { @JoinColumn(name = "contract_id") })
	private List<Contract> contracts;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public List<Client> getClients() {
		List<Client> res = new ArrayList<Client>();
		for (Contract c : getContracts())
			res.add(c.getClient());
		return res;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public Date getDate() {
		if(date==null){
			date = new Date();
			if(getComment()==null) setComment("");
			setComment(getComment().concat(" !! дату надо проверить"));
		}
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public List<Contract> getContracts() {
		if (contracts == null)
			contracts = new ArrayList<Contract>();
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public boolean addContract(Contract con) {
		int conId = con.getId();
		boolean success = true;

		for (Contract c : getContracts()) {
			if (c.getId() == conId) {
				success = false;
				break;
			}
		}
		if (success)
			getContracts().add(con);

		return success;
	}

	public int compareTo(Event o) {
		return this.getDate().compareTo(o.getDate());
	}

	public EventType getEventType() {
		return eventType;
	}

	// returns school share from that event
	public int getSchoolShare() {
		int total = 0;
		for (Contract c : getContracts()) {
			if (c.getContractTypeId() == 2) {
				total += 300;
				continue;
			}
			int basicCost = c.getSingleLessonCost();
			int percent = c.getEventType().getShare();
			total += basicCost * percent;
		}

		return total;
	}

	public int getMoney() {
		int total = 0;
		for (Contract c : getContracts())
			total += c.getSingleLessonCost();

		return total;
	}

	public boolean haveContract(Contract con) {
		for(Contract c : getContracts())
			if(c.getId()==con.getId()) return true;
		return false;
	}
}
