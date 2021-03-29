package com.dio.beerstock.controller;

import com.dio.beerstock.DTO.BeerDTO;
import com.dio.beerstock.builder.BeerBuilder;
import com.dio.beerstock.exception.BeerNotFoundException;
import com.dio.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.print.attribute.standard.Media;

import java.util.Collections;

import static com.dio.beerstock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2l;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerServiceMock;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPostIsCalledThenABeerIsCreated() throws Exception {
        // given
        BeerDTO beerDTO = BeerBuilder.builder().build().toBeerDTO();

        // when
        BDDMockito.when(beerServiceMock.createBeer(beerDTO)).thenReturn(beerDTO);


        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                    .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                    .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));

    }

    @Test
    void whenPostIsCalledWithoutRequiredFieldThenAErrorIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerBuilder.builder().build().toBeerDTO();
        beerDTO.setBrand(null);

//        // when
//        BDDMockito.when(beerServiceMock.createBeer(beerDTO)).thenReturn(beerDTO);


        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETisCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();

        // when
        BDDMockito.when(beerServiceMock.findByName(givenBeerDTO.getName())).thenReturn(givenBeerDTO);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + givenBeerDTO.getName())
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(givenBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(givenBeerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(givenBeerDTO.getType().toString())));
    }

    @Test
    void whenGETisCalledWithNoRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();

        // when
        BDDMockito.when(beerServiceMock.findByName(givenBeerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + givenBeerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListisCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();

        // when
        BDDMockito.when(beerServiceMock.listAll()).thenReturn(Collections.singletonList(givenBeerDTO));

        // then
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(givenBeerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(givenBeerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(givenBeerDTO.getType().toString())));
    }
}