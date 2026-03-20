package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;

public interface UpdateCollectUseCase {
    
    Collect execute(Long id, Collect collect);
}
