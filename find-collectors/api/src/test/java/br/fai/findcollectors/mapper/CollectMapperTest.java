package br.fai.findcollectors.mapper;

import br.fai.findcollectors.dto.request.CreateCollectRequest;
import br.fai.findcollectors.dto.request.UpdateCollectRequest;
import br.fai.findcollectors.dto.response.CollectResponse;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.fai.findcollectors.ApiTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollectMapperTest {

    @Test
    void shouldMapCreateRequestAndOnlyReferenceRecyclerById() {
        CreateCollectRequest request = new CreateCollectRequest(
                COLLECT_DATE, List.of("paper", "GLASS"), true, addressRequest(), 8L
        );

        Collect mapped = CollectMapper.toEntity(request);

        assertThat(mapped.getDateAndTime()).isEqualTo(COLLECT_DATE);
        assertThat(mapped.getGarbageType()).containsExactly(GarbageType.PAPER, GarbageType.GLASS);
        assertThat(mapped.isRecurrent()).isTrue();
        assertThat(mapped.getAddress().getCity()).isEqualTo("Itajuba");
        assertThat(mapped.getRecycler().getId()).isEqualTo(8L);
        assertThat(mapped.getRecycler())
                .extracting("email", "password", "name", "personType")
                .containsOnlyNulls();
        assertThat(mapped.getCollector()).isNull();
        assertThat(mapped.getStatus()).isNull();
        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getCreatedAt()).isNull();
        assertThat(mapped.getUpdatedAt()).isNull();
    }

    @Test
    void shouldMapOnlyEditableFieldsFromUpdateRequest() {
        UpdateCollectRequest request = new UpdateCollectRequest(
                COLLECT_DATE, List.of("plastic", "METAL"), false, addressRequest()
        );

        Collect mapped = CollectMapper.toEntity(request);

        assertThat(mapped.getDateAndTime()).isEqualTo(COLLECT_DATE);
        assertThat(mapped.getGarbageType()).containsExactly(GarbageType.PLASTIC, GarbageType.METAL);
        assertThat(mapped.isRecurrent()).isFalse();
        assertThat(mapped.getAddress().getCep()).isEqualTo("37500-000");
        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getStatus()).isNull();
        assertThat(mapped.getRecycler()).isNull();
        assertThat(mapped.getCollector()).isNull();
        assertThat(mapped.getCreatedAt()).isNull();
        assertThat(mapped.getUpdatedAt()).isNull();
    }

    @Test
    void shouldRejectInvalidMaterialWhenValidationIsBypassed() {
        UpdateCollectRequest request = new UpdateCollectRequest(
                COLLECT_DATE, List.of("UNKNOWN"), false, addressRequest()
        );

        assertThatThrownBy(() -> CollectMapper.toEntity(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("invalid garbageType");
    }

    @Test
    void shouldRejectInvalidCreateMaterialWhenValidationIsBypassed() {
        CreateCollectRequest request = new CreateCollectRequest(
                COLLECT_DATE, List.of("UNKNOWN"), false, addressRequest(), 8L
        );

        assertThatThrownBy(() -> CollectMapper.toEntity(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("invalid garbageType");
    }

    @Test
    void shouldMapCollectToPublicResponse() {
        CollectResponse response = CollectMapper.toResponse(collect());

        assertThat(response.id()).isEqualTo(4L);
        assertThat(response.dateAndTime()).isEqualTo(COLLECT_DATE);
        assertThat(response.garbageType()).containsExactly("PAPER", "METAL");
        assertThat(response.status()).isEqualTo("ACCEPTED");
        assertThat(response.recurrent()).isTrue();
        assertThat(response.address().city()).isEqualTo("Itajuba");
        assertThat(response.collector().id()).isEqualTo(3L);
        assertThat(response.collector().name()).isEqualTo("Collector");
        assertThat(response.collector().personType()).isEqualTo("COLLECTOR");
        assertThat(response.recycler().id()).isEqualTo(1L);
        assertThat(response.recycler().name()).isEqualTo("Recycler");
        assertThat(response.recycler().personType()).isEqualTo("RECYCLER");
        assertThat(response.createdAt()).isEqualTo(CREATED_AT);
        assertThat(response.updatedAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void shouldMapNullableCollectFields() {
        Collect source = collect();
        source.setGarbageType(null);
        source.setCollector(null);
        source.setRecycler(null);
        source.setAddress(null);

        CollectResponse response = CollectMapper.toResponse(source);

        assertThat(response.garbageType()).isEmpty();
        assertThat(response.collector()).isNull();
        assertThat(response.recycler()).isNull();
        assertThat(response.address()).isNull();
    }

    @Test
    void shouldReturnNullForNullCollect() {
        assertThat(CollectMapper.toResponse(null)).isNull();
    }
}
