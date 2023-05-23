package com.mobiquity.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mobiquity.packer.Packer;

@ExtendWith(MockitoExtension.class)
public class PackerTest {
	
	
	@Test
	public void testPack() throws IOException {
		String filecontent ="81: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€102) (5,30.18,€9) (6,46.34,€48)";
		var result = Packer.pack(filecontent.getBytes());
		assertEquals(result, "5,6");
	}
	
	@Test
	public void testPack_when_no_items_are_ebligible_then_expect_empy() throws IOException {
		String filecontent ="8: (1,15.3,€34)";
		var result = Packer.pack(filecontent.getBytes());
		assertEquals(result, "-");
	}
	
	@Test
	public void testPack_when_item_cost_is_greater_than_cost_limit_100_then_expect_empy() throws IOException {
		String filecontent ="8: (1,15.3,€102)";
		var result = Packer.pack(filecontent.getBytes());
		assertEquals(result, "-");
	}
	
	@Test
	public void testPack_when_incorrect_line_then_expect_error() throws IOException {
		String filecontent ="8e: (1,15.3,€102)";
		assertThatThrownBy(()->Packer.pack(filecontent.getBytes())).isInstanceOf(ApiException.class);
	}

}
