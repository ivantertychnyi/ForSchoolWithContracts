package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.TeacherAddition;

@Import(stylesheet = "context:/layout/payroll.css")
public class Payroll {

	@Inject
	@Property
	private TeacherMed tM;
	@Inject
	private EventMed eM;
	@Inject
	private ContractMed cM;

	private TeacherAddition addition;
	@Property
	@Persist
	private Date dateOne;
	@Property
	@Persist
	private Date dateTwo;
	@Property
	private Contract contract;
	private int iteration;
	private int totalMoney = 0;

	void onActivate(int teacherId) {
		tM.setId(teacherId);
	}

	int onPassivate() {
		return tM.getId();
	}

	void setupRender() {
		iteration = 0;
		addition = tM.getAddition();
		setDates();
	}

	private void setDates() {
		Calendar c = new GregorianCalendar();
		c.set(2012, 1, 1);
		dateOne = new Date(c.getTimeInMillis());
		c.set(2012, 9, 1);
		dateTwo = new Date(c.getTimeInMillis());
	}

	public int getIteration() {
		cM.setUnitId(contract.getId());
		totalMoney += getLessonPrice() * getLessonsNumber();
		return ++iteration;
	}

	public Date getToday() {
		return new Date();
	}

	public String getField1() {
		return addition.getField_1();
	}

	public String getField2() {
		return addition.getField_2();
	}

	public String getField3() {
		return addition.getField_3();
	}

	public String getField4() {
		return addition.getField_4();
	}

	public String getField5() {
		return addition.getField_5();
	}

	public List<Contract> getContracts() {
		List<Event> source = raw();
		filter(source);
		List<Contract> contracts = toContracts(source);

		return contracts;
	}

	private List<Event> raw() {
		// step1
		eM.reset();
		return eM.filter(dateOne, dateTwo).filter(tM.getUnit()).getGroup();
	}

	private void filter(List<Event> events) {

	}

	List<Contract> toContracts(List<Event> source) {
		List<Contract> contracts = new ArrayList<Contract>();
		while (source.size() > 0) {
			Event init = source.get(source.size() - 1);
			List<Contract> cts = init.getContracts();

			Contract c = new Contract();
			contracts.add(c);
			Contract t = cts.get(cts.size() - 1);
			cts.remove(cts.size() - 1);
			if (cts.size() == 0)
				source.remove(source.size() - 1);
			c.setId(t.getId());
			c.setTypeId(t.getTypeId());
			c.getEvents().add(init);

			for (int i = source.size() - 1; i >= 0; i--) {
				cts = source.get(i).getContracts();
				for (int j = cts.size() - 1; j > -1; j--)
					if (cts.get(j).getId() == c.getId()) {
						c.getEvents().add(source.get(i));
						cts.remove(j);
						if (cts.size() == 0)
							source.remove(i);
					}
			}
		}
		return contracts;
	}

	public String getName() {
		return cM.getClientName();
	}

	public String getType() {
		return eM.loadEventType(contract.getTypeId()).getTypeTitle();
	}

	public int getLessonPrice() {
		return eM.loadEventType(contract.getTypeId()).getShareTeacher();
	}

	public int getLessonsNumber() {
		return contract.getEvents().size();
	}
	
	public int getTotalMoney(){
		return totalMoney;
	}
	public int getTax(){
		return totalMoney*13/100;
	}
	public int getTaxed(){
		return getTotalMoney()-getTax();
	}
}

























