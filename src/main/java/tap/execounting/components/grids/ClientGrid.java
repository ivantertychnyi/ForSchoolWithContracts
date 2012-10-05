package tap.execounting.components.grids;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddClient;
import tap.execounting.components.show.SmartIcon;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Teacher;
import tap.execounting.security.AuthorizationDispatcher;

public class ClientGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CRUDServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private AddClient editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Client> model;
	@Property
	private Client unit;
	@Property
	@Persist
	private String nameFilter;

	public List<Client> getSource() {
		List<Client> list = dao.findWithNamedQuery(Client.ALL);
		if (nameFilter != null && nameFilter.length() > 2)
			for (int i = list.size() - 1; i >= 0; i--)
				if (!list.get(i).getName().toLowerCase()
						.contains(nameFilter.toLowerCase()))
					list.remove(i);
		return list;
	}

	Object onActionFromEdit(Client c) {
		// AUTHORIZATION MOUNT POINT EDIT
		if (dispatcher.canEditClients()) {
			editorActive = true;
			editor.setup(c);
		}
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT CREATE
		if (dispatcher.canCreateClients()) {
			editorActive = true;
			editor.reset();
		}
		return ezone.getBody();
	}

	void onDelete(Client c) {
		// AUTHORIZATION MOUNT POINT DELETE
		if (dispatcher.canDeleteClients()) {
			if (c.getContracts().size() > 0)
				// TODO JAVASCRIPT ALERT MOUNT POINT
				throw new IllegalArgumentException(
						"У данного клиента заключены с вами договора, пожалуйста сначала удалите их.");
			else
				dao.delete(Client.class, c.getId());
		}
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			model.exclude("return", "balance", "state", "firstContractDate",
					"studentInfo", "firstPlannedPaymentDate", "date", "id");
			model.add("teachers", null);
			model.add("Action", null);
			model.add("deleted");
			model.reorder("deleted");
		}
	}

	public String getTeachers() {
		StringBuilder sb = new StringBuilder();
		List<Teacher> ts = unit.getCurrentTeachers();
		for (int i = 0; i < ts.size(); i++) {
			sb.append(ts.get(i).getName());
			if (i < ts.size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}
	public String getIconType(){
		return unit.isDeleted() ? SmartIcon.DELETED : "";
	}
}
