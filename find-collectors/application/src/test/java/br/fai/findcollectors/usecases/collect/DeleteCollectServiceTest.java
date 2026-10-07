package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.NotFoundException;
import br.fai.findcollectors.repositories.CollectRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteCollectServiceTest {
    private final CollectRepository repository = mock(CollectRepository.class);
    private final DeleteCollectService service = new DeleteCollectService(repository);

    @Test
    void shouldDeleteUsingFoundCollectId() {
        when(repository.findById(20L)).thenReturn(Optional.of(Collect.builder().id(21L).build()));
        service.execute(20L);
        verify(repository).deleteById(21L);
    }

    @Test
    void shouldRejectMissingCollectWithoutDeleting() {
        when(repository.findById(20L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.execute(20L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Collect with id 20 not found")
                .extracting("code").isEqualTo(ErrorCode.COLLECT_NOT_FOUND);
        verify(repository, never()).deleteById(anyLong());
    }
}
