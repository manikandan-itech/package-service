package com.mobiquity.packer;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Item {
	int index;
	int weight;
	int cost;
}
