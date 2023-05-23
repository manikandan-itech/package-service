package com.mobiquity.packer;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Package {
	int weight;
	List<Item> items;
}
