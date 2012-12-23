package tap.execounting.components.show;

import java.text.SimpleDateFormat;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import tap.execounting.entities.Contract;

@Import(stylesheet = "context:css/minicontract.css")
public class MiniContract {

	@Parameter
	@Property
	private Contract contract;

	@Parameter
	@Property
	private boolean displayName;

	public String getContractDate() {
		return "От: "
				+ new SimpleDateFormat("d MMMM yy").format(contract.getDate());
	}

	public String getType() {
		return contract.getEventType().getTitle();
	}

	public String getEventsInfo() {
		return "Проведено "
				+ (contract.getLessonsNumber() - contract.getLessonsRemain())
				+ " занятий из " + contract.getLessonsNumber();
	}

	public String getPaymentsInfo() {
		return "Оплачено " + contract.getMoneyPaid() + " из "
				+ contract.getMoney();
	}
	
	public String getState(){
		return "Состояние договора: " + contract.getState().toString();
	}
	
	public String getUrl(){
		return "ClientPage/"+contract.getClientId()+"#contractBody"+contract.getId();
	}
}
