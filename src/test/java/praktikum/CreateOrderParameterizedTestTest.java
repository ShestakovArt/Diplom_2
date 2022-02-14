package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.response.CreateOrderResponse;
import praktikum.response.CreateUserResponse;
import praktikum.response.ResponseErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreateOrderParameterizedTestTest {

    public UserRequest userRequest;
    public OrderRequest orderRequest;
    private String accessToken;
    private boolean authorization;
    private boolean availabilityIngredients;
    private boolean ingredientsCorrectHash;

    public CreateOrderParameterizedTestTest(boolean authorization, boolean availabilityIngredients, boolean ingredientsCorrectHash) {
        this.authorization = authorization;
        this.availabilityIngredients = availabilityIngredients;
        this.ingredientsCorrectHash = ingredientsCorrectHash;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                {true, true, true},
                {true, false, false},
                {true, true, false},
                {false, true, true},
                {false, false, false},
                {false, true, false},
        };
    }

    @Before
    public void setup() {
        userRequest = new UserRequest();
        orderRequest = new OrderRequest();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userRequest.deleteUser(accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Creating an order")
    @Description("Создание заказа")
    public void testCreatingOrder() {
        User user = User.getRandom();
        if (authorization == true) {
            Response responseCreate = userRequest.createUserResponse(user);
            accessToken = responseCreate.body().as(CreateUserResponse.class).getAccessToken();
            responseCreate.then().statusCode(SC_OK);

            Response responseAuthorization = userRequest.authorizationUserResponse(user);
            responseAuthorization.then().statusCode(SC_OK);
        }

        List<String> ingredients = List.of();

        if (availabilityIngredients) {
            if (ingredientsCorrectHash) {
                ingredients = List.of("61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa76", "61c0c5a71d1f82001bdaaa71");
            } else if (!(ingredientsCorrectHash)) {
                ingredients = List.of("123456789012345678901234", "null", "123456789012345678901234", "null");
            }
        }

        Order order = new Order(ingredients);

        Response responseOrder;
        if (authorization == true) {
            responseOrder = orderRequest.createOrderResponse(accessToken.substring(7), order);
        } else {
            responseOrder = orderRequest.createOrderResponse(order);
        }

        if (availabilityIngredients) {
            if (ingredientsCorrectHash) {
                responseOrder.then().statusCode(SC_OK);
                Assert.assertTrue("Неверное тело ответа", responseOrder.body().as(CreateOrderResponse.class).isSuccess());
                if (authorization == true) {
                    Assert.assertEquals("Имя пользователя не совпадает", user.name, responseOrder.body().as(CreateOrderResponse.class).getOrder().getOwner().getName());
                }
            } else if (!(ingredientsCorrectHash)) {
                responseOrder.then().statusCode(SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            responseOrder.then().statusCode(SC_BAD_REQUEST);
            ResponseErrorMessage bodyResponseErrorMessage = responseOrder.body().as(ResponseErrorMessage.class);
            Map<String, String> invalidRequestDataMap = new HashMap<>();
            invalidRequestDataMap.put("success", "false");
            invalidRequestDataMap.put("message", "Ingredient ids must be provided");

            Assert.assertEquals("Не верное тело ответа", invalidRequestDataMap.toString(), bodyResponseErrorMessage.toString());
        }
    }
}