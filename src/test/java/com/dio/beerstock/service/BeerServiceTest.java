package com.dio.beerstock.service;

import com.dio.beerstock.DTO.BeerDTO;
import com.dio.beerstock.builder.BeerBuilder;
import com.dio.beerstock.entity.Beer;
import com.dio.beerstock.exception.BeerAlreadyRegisteredException;
import com.dio.beerstock.exception.BeerNotFoundException;
import com.dio.beerstock.mapper.BeerMapper;
import com.dio.beerstock.repository.BeerRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Teste for BeerService")
class BeerServiceTest {

    private static final Long INVALID_BEER_ID = 1l;

    @InjectMocks
    private BeerService beerService;
    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @Mock   // sempre da classe que pretendemos "simular"
    private BeerRepository beerRepositoryMock;

//    @BeforeEach // compilar antes de todos testes
//    void setUp() {
//        // prepara o ambiente, o que é "fornecido"
//        BeerDTO beerDTO = BeerBuilder.builder().build().toBeerDTO();
//        Beer expectedSavedBeer = beerMapper.toModel(beerDTO);
//
//        // o que fazer quando chamar a classe mockada (simulada)
//        BDDMockito.when(beerRepositoryMock.findByName(beerDTO.getName()))
//                .thenReturn(Optional.of(expectedSavedBeer));
//        BDDMockito.when(beerRepositoryMock.save(expectedSavedBeer))
//                .thenReturn(expectedSavedBeer);
//    }

    @Test
    @DisplayName(value = "Should create a beer when it is informed")
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO givenBeer = BeerBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(givenBeer);

        // when
        BDDMockito.when(beerRepositoryMock.findByName(givenBeer.getName())).thenReturn(Optional.empty());
        BDDMockito.when(beerRepositoryMock.save(ArgumentMatchers.any(Beer.class))).thenReturn(expectedBeer);

        // then
        BeerDTO testedBeer = beerService.createBeer(givenBeer);

        MatcherAssert.assertThat(testedBeer.getId(),
                Matchers.is(Matchers.equalTo(givenBeer.getId())));

        Assertions.assertThat(testedBeer).isNotNull().isEqualTo(givenBeer);
        Assertions.assertThat(testedBeer.getQuantity()).isGreaterThan(2);

        assertEquals(givenBeer.getId(), testedBeer.getId());
        assertEquals(givenBeer.getName(), testedBeer.getName());
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenExceptionShouldBeThrown() {
        // given
        BeerDTO givenBeer = BeerBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(givenBeer);

        // when
        BDDMockito.when(beerRepositoryMock.findByName(givenBeer.getName()))
                .thenReturn(Optional.of(duplicatedBeer));

        // then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(givenBeer));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();
        Beer givenBeer = beerMapper.toModel(givenBeerDTO);
        
        // when
        BDDMockito.when(beerRepositoryMock.findByName(givenBeer.getName()))
                .thenReturn(Optional.of(givenBeer));
        
        // then
        BeerDTO foundBeerDTO = beerService.findByName(givenBeerDTO.getName());

        Assertions.assertThat(foundBeerDTO).isEqualTo(givenBeerDTO);
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrownAnException() {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();

        // when
        BDDMockito.when(beerRepositoryMock.findByName(givenBeerDTO.getName()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(givenBeerDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();
        Beer givenBeer = beerMapper.toModel(givenBeerDTO);

        // when
        BDDMockito.when(beerRepositoryMock.findAll()).thenReturn(Collections.singletonList(givenBeer));

        // then
        List<BeerDTO> foundBeerDTO = beerService.listAll();

        Assertions.assertThat(foundBeerDTO).isNotEmpty();
        Assertions.assertThat(foundBeerDTO.get(0)).isEqualTo(givenBeerDTO);

    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyList() {
        // when
        BDDMockito.when(beerRepositoryMock.findAll()).thenReturn(Collections.EMPTY_LIST);

        // then
        List<BeerDTO> foundBeerDTO = beerService.listAll();

        Assertions.assertThat(foundBeerDTO).isEmpty();
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
        // given
        BeerDTO givenBeerDTO = BeerBuilder.builder().build().toBeerDTO();
        Beer givenBeer = beerMapper.toModel(givenBeerDTO);

        // when
        BDDMockito.when(beerRepositoryMock.findById(givenBeerDTO.getId())).thenReturn(Optional.of(givenBeer));
        BDDMockito.doNothing().when(beerRepositoryMock).deleteById(givenBeerDTO.getId());
        // then
        beerService.deleteById(givenBeerDTO.getId());

        BDDMockito.verify(beerRepositoryMock, Mockito.times(1)).findById(givenBeer.getId());
        BDDMockito.verify(beerRepositoryMock, Mockito.times(1)).deleteById(givenBeer.getId());
    }
}