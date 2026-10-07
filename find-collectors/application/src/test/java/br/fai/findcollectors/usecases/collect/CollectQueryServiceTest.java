package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.CollectRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CollectQueryServiceTest {
    private final CollectRepository repository = mock(CollectRepository.class);
    private final CollectQueryService service = new CollectQueryService(repository);

    @Test
    void shouldReturnAllCollects() {
        List<Collect> collects = List.of(Collect.builder().id(1L).build());
        when(repository.find()).thenReturn(collects);
        assertThat(service.find()).isSameAs(collects);
    }

    @Test
    void shouldReturnCollectById() {
        Collect collect = Collect.builder().id(8L).build();
        when(repository.findById(8L)).thenReturn(Optional.of(collect));
        assertThat(service.findById(8L)).isSameAs(collect);
    }

    @Test
    void shouldRejectMissingCollectById() {
        when(repository.findById(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(8L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Collect with id 8 not found")
                .extracting("code").isEqualTo(ErrorCode.COLLECT_NOT_FOUND);
    }

    @Test
    void shouldDelegatePendingCollectsLookup() {
        List<Collect> collects = List.of(Collect.builder().id(1L).build());
        when(repository.findPendingCollects(3L)).thenReturn(collects);
        assertThat(service.findPendingCollects(3L)).isSameAs(collects);
        verify(repository).findPendingCollects(3L);
    }

    @Test
    void shouldDelegateMyCollectsLookup() {
        List<Collect> collects = List.of(Collect.builder().id(2L).build());
        when(repository.findMyCollects(4L)).thenReturn(collects);
        assertThat(service.findMyCollects(4L)).isSameAs(collects);
        verify(repository).findMyCollects(4L);
    }

    @Test
    void shouldDelegateAcceptedCollectsLookup() {
        List<Collect> collects = List.of(Collect.builder().id(3L).build());
        when(repository.findCollectorAcceptedCollects(5L)).thenReturn(collects);
        assertThat(service.findCollectorAcceptedCollects(5L)).isSameAs(collects);
        verify(repository).findCollectorAcceptedCollects(5L);
    }
}
