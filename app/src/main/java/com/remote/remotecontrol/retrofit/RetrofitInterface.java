package com.remote.remotecontrol.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Account Payable Retrofit 요청 API 명세 Interface
 * @author 박상원
 * @since 2020. 11.27
 * @version 1.1
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *      수정일		   수정자             수정내용
 *  ============    ==========    =======================
 *  2020. 11. 27     박상원              최초 생성
 *
 * =======================================================
 *
 * </pre>
 */
public interface RetrofitInterface {

    // get AuthenticateUser
    @POST("/quicksetlite.svc/Authenticateuser")
    Call<UeiRcModel> getUeiRc(@Header("Content-Type") String contentType, @Body UserUeiRC param);

    // get Brands
    @POST("/quicksetlite.svc/GetBrandsLike")
    Call<BrandModel> getBrand(@Header("Content-Type") String contentType, @Body Brands param);

    // get Test Key
    @POST("/quicksetlite.svc/LoadOSMAndGetNextKey")
    Call<NextKeyModel> getTestKey(@Header("Content-Type") String contentType, @Body Nextkey param);

    // hax String send
    @POST("/ws/file/convert")
    Call<STTModel> convertIo(@Header("Content-Type") String contentType, @Body ConvertIO param);



    // get Test Key
    @POST("/ws/stt")
    Call<SpeechModel> getConverter(@Header("Content-Type") String contentType, @Body Convert param);

    /*// get Test Key
    @POST("/ws/file/convert")
    Call<SpeechModel> getConverter(@Header("Content-Type") String contentType, @Body Convert param);*/

}
