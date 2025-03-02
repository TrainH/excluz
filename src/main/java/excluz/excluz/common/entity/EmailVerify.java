package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "email_verifys")
@NoArgsConstructor
public class EmailVerify {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private Boolean emailStatus;

	public EmailVerify(String email) {
		this.email = email;
		this.emailStatus = false;
	}

	public void updateEmailStatus(Boolean emailStatus) {this.emailStatus = emailStatus;}
}
