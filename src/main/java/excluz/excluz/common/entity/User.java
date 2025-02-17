package excluz.excluz.common.entity;

import org.hibernate.annotations.Comment;
import org.hibernate.usertype.UserType;

import excluz.excluz.domain.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Comment("유저 실명")
	@Column(length = 30, nullable = false)
	private String name;

	@Comment("유저 별명")
	@Column(name = "nick_name", length = 10, unique = true, nullable = false) // 중복 닉네임 X
	private String nickName;

	@Comment("유저 전화번호")
	@Column(name = "phone_number",columnDefinition = "CHAR(15)", unique = true, nullable = false) // 중복 전화번호 X
	private String phoneNumber;

	@Comment("유저 집주소")
	@Column(length = 100, nullable = false)
	private String address;

	@Comment("유저 이메일")
	@Column(length = 50, unique = true, nullable = false)
	private String email;

	@Comment("유저 비밀번호")
	@Column(length = 60, nullable = false)
	private String password;

	@Comment("유저 타입")
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Comment("유저 탈퇴 여부")
	@Column(name = "is_deleted", columnDefinition = "TINYINT")
	private Boolean isDeleted;

	@Builder // 매개변수가 4개이상은 빌더 패턴을 사용.
	public User(
				String name,
				String nickName,
				String phoneNumber,
				String address,
				String email,
				String password) {
		this.name = name;
		this.nickName = nickName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.email = email;
		this.password = password;
		this.userRole = UserRole.CUSTOMER;
		this.isDeleted = false;
	}

	// 회원 탈퇴 시 변경될 유저 상태
	public void updateUserStatus(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateUserProfile(
			String nickName,
			String phoneNumber,
			String address,
			String email ) {
		this.nickName = nickName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.email = email;
	}
}
