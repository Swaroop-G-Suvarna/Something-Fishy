package info.accolade.fishing_master.utils;

import java.util.List;

import info.accolade.fishing_master.modal.CategoryModal;
import info.accolade.fishing_master.modal.DefaultResponceModal;
import info.accolade.fishing_master.modal.EmergencyModal;
import info.accolade.fishing_master.modal.LoginResponse;
import info.accolade.fishing_master.modal.MagazineModal;
import info.accolade.fishing_master.modal.RequestModal;
import info.accolade.fishing_master.modal.RescueModal;
import info.accolade.fishing_master.modal.SearchModal;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("register.php")
    Call<DefaultResponceModal> getResponce(
            @Field("name") String name,
            @Field("number") String number,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> getLoginResponce(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("forgot.php")
    Call<DefaultResponceModal> getforgotResponce(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("feedback.php")
    Call<DefaultResponceModal> getFeedbackResponce(
            @Field("ratings") String rating,
            @Field("descriptions") String feedback,
            @Field("userid") String userid
    );

    @POST("magazine.php")
    Call<List<MagazineModal>> getMagazineResponse();

    @FormUrlEncoded
    @POST("rescue.php")
    Call<List<RescueModal>> getRescueData(
            @Field("radius") String radius,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @POST("rescuelocation.php")
    Call<List<RescueModal>> getRescueLocationData();

    @POST("emergency.php")
    Call<List<EmergencyModal>> getEmergencyResponse();

    @POST("search.php")
    Call<List<SearchModal>> getSearchResponse();

    @FormUrlEncoded
    @POST("request.php")
    Call<DefaultResponceModal> getRequestResponce(
            @Field("UserId") String userid,
            @Field("BoatNumber") String boat,
            @Field("FishName") String fish
    );

    @FormUrlEncoded
    @POST("viewrequest.php")
    Call<List<RequestModal>> getViewRequestData(
            @Field("BoatNumber") String num
    );

    @FormUrlEncoded
    @POST("viewuserrequest.php")
    Call<List<RequestModal>> getViewUserRequestData(
            @Field("uid") String uid
    );

    @FormUrlEncoded
    @POST("addfish.php")
    Call<DefaultResponceModal> getFishResponce(
            @Field("name") String name,
            @Field("desc") String desc,
            @Field("date") String date,
            @Field("nameimg") String password,
            @Field("img") String img,
            @Field("boatname") String boatname,
            @Field("boatno") String boatno
    );


}
