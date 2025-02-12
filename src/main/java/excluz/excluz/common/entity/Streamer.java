package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "streamers")
@NoArgsConstructor
public class Streamer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(name = "nick_name", nullable = false, unique = true, length = 10)
	private String nickName;

	@Column(name = "phone_number", nullable = false, unique = true, columnDefinition = "char(15)")
	private String phoneNumber;

	@Column(nullable = false, unique = true, length = 50)
	private String email;

	@Column(nullable = false, length = 30)
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Builder
	public Streamer(String name,
		String nickName,
		String phoneNumber,
		String email,
		String password) {
		this.name = name;
		this.nickName = nickName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.userRole = UserRole.STREAMER;
		this.isDeleted = false;
	}

	public void updateIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
