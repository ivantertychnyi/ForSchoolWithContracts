package tap.execounting.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.NonVisual;
/**
 * This class does not support interface entities.interfaces.Deletable,
 * since some comments certainly should be removed.
 * @author truth0
 *
 */
@Entity
@Table(name = "comment")
@NamedQueries({
@NamedQuery(name = Comment.ALL,query="from Comment"),
@NamedQuery(name=Comment.BY_TEACHER_ID,query="from Comment where code=0 and entityId=:teacherId")
})
public class Comment {
	public static final String ALL = "Comment.all";
	public static final String BY_TEACHER_ID = "Comment.byTeacherId";
	
	public static final int TeacherCode = 0;
	
	//service properties
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NonVisual
	private int id;
	@NonVisual
	private int code;
	@NonVisual
	private int entityId;
	@NonVisual
	private boolean deleted;	
	//business properties
	private String text;
	@NonVisual
	private Date date;
	@NonVisual
	private int userId;

	
	public Comment() {
	}
	
	public Comment(int code, int userId, int entityId){
		this.code = code;
		this.userId = userId;
		this.entityId = entityId;
	}
	
	public void append(Date date, String text){
		this.date = date;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
