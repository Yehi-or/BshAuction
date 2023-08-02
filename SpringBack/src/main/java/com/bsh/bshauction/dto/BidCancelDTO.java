package com.bsh.bshauction.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BidCancelDTO {
    private List<BidCancelInfoDTO> selectedBids;
    private String bidMessageAccessToken;

}
