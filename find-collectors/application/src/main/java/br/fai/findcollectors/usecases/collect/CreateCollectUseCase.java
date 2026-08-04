package br.fai.findcollectors.usecases.collect;

import br.fai.findcollectors.entities.Collect;

public interface CreateCollectUseCase {
    
    Collect execute(Collect collect);
}
