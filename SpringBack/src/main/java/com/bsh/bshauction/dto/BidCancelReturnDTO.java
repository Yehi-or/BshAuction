package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BidCancelReturnDTO {
    private List<BidCancelInfoDTO> bidCancelInfoDTOList;
    private String returnMessage;
}
