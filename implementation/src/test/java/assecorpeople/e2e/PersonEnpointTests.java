package assecorpeople.e2e;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PersonEnpointTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetPersonsListSuccess() throws Exception {

        mvc.perform(get("/persons").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(hasKey("id")));

    }

    @Test
    public void testGetPersonSuccess() throws Exception {

        int id = 12;
        mvc.perform(get("/persons/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasKey("id")))
                .andExpect(jsonPath("$").value(hasKey("vorname")))
                .andExpect(jsonPath("$").value(hasKey("nachname")))
                .andExpect(jsonPath("$").value(hasKey("city")))
                .andExpect(jsonPath("$").value(hasKey("zipcode")))
                .andExpect(jsonPath("$").value(hasKey("color")))
                .andExpect(jsonPath("$.id").value(is(id)));
    }

    @Test
    public void testGetPersonNotFound() throws Exception {
        int id = -15;
        mvc.perform(get("/persons/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostPerson() throws Exception {

        String firstname = "testvorname321";
        String colorName = "Color1";
        MvcResult mvcResult = mvc.perform(post("/persons")
                .param("vorname", firstname)
                .param("nachname", "testPostPerson")
                .param("zipcode", "testPostPerson")
                .param("city", "testPostPerson")
                .param("color", colorName))

                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vorname", is(firstname)))
                .andExpect(jsonPath("$.color", is(colorName)));

    }


}
