package com.bsh.bshauction.repository;

import java.time.LocalDateTime;

public interface ProductRepositoryCustom {
    boolean updateProductFinishTime(Long productId, LocalDateTime newUpdateFinishTime);
}
