package excluz.excluz.common.datas;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;

/* 공유 데이터 */
public class SharedData {

	// Streamer 데이터 1
	public final static Integer STREAMER_ID1 = 1;
	public final static String STREAMER_NAME1 = "홍길동";
	public final static String STREAMER_NAME2 = "김첨지";
	public final static String STREAMER_NICKNAME1 = "암행어사";
	public final static String STREAMER_NICKNAME2 = "운수좋은날";
	public final static String STREAMER_PHONE_NUMBER1 = "010-1234-1234";
	public final static String STREAMER_PHONE_NUMBER2 = "010-7777-7777";
	public final static String STREAMER_EMAIL1 = "test12@test.com";
	public final static String STREAMER_EMAIL2 = "test77@test.com";
	public final static String STREAMER_PASSWORD1 = "Qwer1234!!!!";
	public final static String STREAMER_PASSWORD2 = "Qwer5678!!!!";
	public final static String STREAMER_REENTER_PASSWORD1 = "Qwer1234!!!!";
	public final static Streamer STREAMER1 = new Streamer(STREAMER_NAME1, STREAMER_NICKNAME1, STREAMER_PHONE_NUMBER1, STREAMER_EMAIL1, STREAMER_PASSWORD1);
	public final static Streamer STREAMER2 = new Streamer(STREAMER_NAME2, STREAMER_NICKNAME2, STREAMER_PHONE_NUMBER2, STREAMER_EMAIL2, STREAMER_PASSWORD1);
	public final static StreamerSignupRequestDto STREAMER_SIGNUP_REQUEST_DTO = new StreamerSignupRequestDto(STREAMER_NAME1, STREAMER_NICKNAME1, STREAMER_PHONE_NUMBER1, STREAMER_EMAIL1, STREAMER_PASSWORD1, STREAMER_REENTER_PASSWORD1);
	public final static StreamerLoginRequestDto STREAMER_LOGIN_REQUEST_DTO = new StreamerLoginRequestDto(STREAMER_EMAIL1, STREAMER_PASSWORD1);

	// Streamer 데이터 2
	public final static Integer STREAMER_ID2 = 2;
//	public final static String STREAMER_NAME2 = "이순신";
//	public final static String STREAMER_NICKNAME2 = "거북선";
//	public final static String STREAMER_PHONE_NUMBER2 = "010-5678-5678";
//	public final static String STREAMER_EMAIL2 = "test34@test.com";
//	public final static String STREAMER_PASSWORD2 = "Asdf5678!!!!";
	public final static String STREAMER_REENTER_PASSWORD2 = "Asdf5678!!!!";
//	public final static Streamer STREAMER2 = new Streamer(STREAMER_NAME2, STREAMER_NICKNAME2, STREAMER_PHONE_NUMBER2, STREAMER_EMAIL2, STREAMER_PASSWORD2);
	public final static StreamerSignupRequestDto STREAMER_SIGNUP_REQUEST_DTO2 = new StreamerSignupRequestDto(STREAMER_NAME2, STREAMER_NICKNAME2, STREAMER_PHONE_NUMBER2, STREAMER_EMAIL2, STREAMER_PASSWORD2, STREAMER_REENTER_PASSWORD2);
	public final static StreamerLoginRequestDto STREAMER_LOGIN_REQUEST_DTO2 = new StreamerLoginRequestDto(STREAMER_EMAIL2, STREAMER_PASSWORD2);

	// Store 데이터
	public final static Integer STORE_ID1 = 1;
	public final static String ADDRESS1 = "사랑시 평화동 정의로12";
	public final static String ADDRESS2 = "희망시 보람동 성공로12";
	public final static String STORE_NAME1 = "테스트샵";
	public final static String STORE_NAME2 = "업데이트샵";
	public final static String REGISTRATION_NUMBER1 = "123-12-12345";
	public final static String REGISTRATION_NUMBER2 = "678-67-67890";
	public final static Store STORE1 = new Store(STREAMER1, ADDRESS1, STORE_NAME1,REGISTRATION_NUMBER1);

	// Item 데이터
	public final static Integer ITEM_ID1 = 1;
	public final static String ITEM_NAME1 = "테스트 굿즈";
	public final static String ITEM_NAME2 = "업데이트 굿즈";
	public final static String EXPLANATION1 = "굿즈 설명";
	public final static String EXPLANATION2 = "업데이트 굿즈 설명";
	public final static Integer PRICE1 = 3000;
	public final static Integer PRICE2 = 4000;
	public final static Integer REMAINING_QUANTITY1 = 100;
	public final static Integer REMAINING_QUANTITY2 = 200;
	public final static Item UPDATED_ITEM = new Item(STORE1, ITEM_NAME2, EXPLANATION2, PRICE2, REMAINING_QUANTITY2);
	public final static Item ITEM1 = new Item(STORE1, ITEM_NAME1, EXPLANATION1, PRICE1, REMAINING_QUANTITY1);
	public final static Item ITEM2 = new Item(STORE1, ITEM_NAME2, EXPLANATION2, PRICE2, REMAINING_QUANTITY2);
	public final static ItemCreateRequestDto ITEM_CREATE_REQUEST_DTO = new ItemCreateRequestDto(ITEM_NAME1, EXPLANATION1, PRICE1, REMAINING_QUANTITY1);
	public final static ItemUpdateRequestDto ITEM_UPDATE_REQUEST_DTO = new ItemUpdateRequestDto(ITEM_NAME2, EXPLANATION2, PRICE2, REMAINING_QUANTITY2);
	public final static ItemResponseDto ITEM_RESPONSE_DTO = new ItemResponseDto(ITEM_ID1, ITEM_NAME2, EXPLANATION2, PRICE2, REMAINING_QUANTITY2);

	// User 데이터
	public final static Integer USER_ID = 1;
	public final static String USER_NAME1 = "홍길동";
	public final static String USER_NICKNAME1 = "암행어사";
	public final static String UPDATE_NICKNAME2 = "엄행어사";
	public final static String USER_PHONE_NUMBER1 = "010-1234-1234";
	public final static String USER_PHONE_NUMBER2 = "010-5678-1234";
	public final static String USER_ADDRESS1 = "사랑시 평화동 정의로12";
	public final static String USER_ADDRESS2 = "사랑시 고백구 행복4길 4-1";
	public final static String USER_EMAIL1 = "test12@test.com";
	public final static String USER_PASSWORD1 = "Qwer1234!!!!";
	public final static String USER_REENTER_PASSWORD1 = "Qwer1234!!!!";
	public final static String USER_PASSWORD2 = "Qwer1234!@@!";
	public final static String USER_REENTER_PASSWORD2 = "Qwer1234!@@!";
	public final static UserSignupRequestDto USER_SIGNUP_REQUEST_DTO = new UserSignupRequestDto(USER_NAME1, USER_NICKNAME1, USER_PHONE_NUMBER1, USER_ADDRESS1, USER_EMAIL1, USER_PASSWORD1, USER_REENTER_PASSWORD1);
}
