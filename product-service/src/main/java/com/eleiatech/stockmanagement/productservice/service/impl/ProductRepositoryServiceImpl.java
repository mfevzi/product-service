package com.eleiatech.stockmanagement.productservice.service.impl;

import com.eleiatech.stockmanagement.productservice.enums.Language;
import com.eleiatech.stockmanagement.productservice.exception.enums.FriendlyMessageCodes;
import com.eleiatech.stockmanagement.productservice.exception.exceptions.ProductNotCreatedException;
import com.eleiatech.stockmanagement.productservice.exception.exceptions.ProductNotFoundException;
import com.eleiatech.stockmanagement.productservice.repository.ProductRepository;
import com.eleiatech.stockmanagement.productservice.repository.entity.Product;
import com.eleiatech.stockmanagement.productservice.request.ProductCreateRequest;
import com.eleiatech.stockmanagement.productservice.request.ProductUpdateRequest;
import com.eleiatech.stockmanagement.productservice.service.IProductRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.This;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRepositoryServiceImpl implements IProductRepositoryService {
	private final ProductRepository productRepository;

	@Override
	public Product createProduct(Language language, ProductCreateRequest productCreateRequest) {
		log.debug("[{}][createProduct] -> request: {}", this.getClass().getSimpleName(), productCreateRequest);
		try {
			Product product = Product.builder().productName(productCreateRequest.getProductName())
					.quantity(productCreateRequest.getQuantity()).price(productCreateRequest.getPrice()).deleted(false)
					.build();
			Product productResponse = productRepository.save(product);
			log.debug("[{}][createProduct] -> response: {}", this.getClass().getSimpleName(), productResponse);
			return productResponse;
		} catch (Exception exception) {
			throw new ProductNotCreatedException(language, FriendlyMessageCodes.PRODUCT_NOT_CREATED_EXCEPTION,
					"product request: " + productCreateRequest.toString());
		}
	}

	@Override
	public Product getProduct(Language language, Long productId) {
		log.debug("[{}][getProduct] -> request productId: {}", this.getClass().getSimpleName(), productId);
		Product product = productRepository.getByProductIdAndDeletedFalse(productId);
		// product bulunamadi ise hata firlatalim
		if (Objects.isNull(product)) {
			throw new ProductNotFoundException(language, FriendlyMessageCodes.PRODUCT_NOT_FOUND,
					"Product not found for product id: " + productId);
		}
		// metodun calistigini ve urun sorgulamasi yapildigini loglayalim
		log.debug("[{}][getProduct] -> response: {}", this.getClass().getSimpleName(), product);
		return product;
	}

	@Override
	public List<Product> getProducts(Language language) {
		log.debug("[{}][getProducts]", this.getClass().getSimpleName());
		List<Product> products = productRepository.getAllByDeletedFalse();
		//liste bos ise hata dondurelim
		if(products.isEmpty()) {
			throw new ProductNotFoundException(language, FriendlyMessageCodes.PRODUCT_NOT_FOUND,
					"Products not found");
		}
		log.debug("[{}][getProducts] -> response: {}", this.getClass().getSimpleName(), products);
		return products;
	}

	@Override
	public Product updateProduct(Language language, Long productId, ProductUpdateRequest productUpdateRequest) {
		log.debug("[{}][updateProduct] -> request productId: {}", this.getClass().getSimpleName(), productUpdateRequest);
		//guncellenecek urunu bul
		Product product = getProduct(language, productId);
		//urunun yeni degerlerini set et
		product.setProductName(productUpdateRequest.getProductName());
		product.setQuantity(productUpdateRequest.getQuantity());
		product.setPrice(productUpdateRequest.getPrice());
		product.setProductCreatedDate(product.getProductCreatedDate());
		product.setProductUpdatedDate(new Date());
		//yeni degerler set edilen urunu kaydedelim
		Product productResponse = productRepository.save(product);
		log.debug("[{}][updateProduct] -> response: {}", this.getClass().getSimpleName(), productResponse);
		return productResponse;
	}

	@Override
	public Product deleteProduct(Language language, Long productId) {
		return null;
	}
}
