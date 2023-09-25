package uz.sardorbroo.keycloak.authenticator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneIdUserDTO {

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("ctzn")
    private String ctzn;

    @JsonProperty("pport_issue_place")
    private String pportIssuePlace;

    @JsonProperty("sur_name")
    private String surname;

    @JsonProperty("gd")
    private String gd;

    @JsonProperty("natn")
    private String natn;

    @JsonProperty("pport_issue_date")
    private String pportIssueDate;

    @JsonProperty("pport_expr_date")
    private String pportExprDate;

    @JsonProperty("pport_no")
    private String pportNo;

    @JsonProperty("pin")
    private String pin;

    @JsonProperty("mob_phone_no")
    private String mobPhoneNo;

    @NotBlank
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("birth_place")
    private String birthPlace;

    @JsonProperty("mid_name")
    private String middleName;

    @JsonProperty("valid")
    private String valid;

    @JsonProperty("user_type")
    private String user_type;

    @JsonProperty("sess_id")
    String sessId;

    @JsonProperty("ret_cd")
    private String retCd;

    @JsonProperty("auth_method")
    private String authMethod;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("full_name")
    private String fullName;
}
