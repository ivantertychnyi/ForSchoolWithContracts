package tap.execounting.data;

public enum ContractState {

	active("активен"),
	frozen("заморожен"),
	canceled("закрыт"),
	complete("завершен"),
	undefined("неопределено");
	
	private String translate;
	
	private ContractState(String translate){
		this.translate = translate;
	}
	
	@Override
	public String toString(){
		return translate;
	}
}
