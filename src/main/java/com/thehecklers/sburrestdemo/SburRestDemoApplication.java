package com.thehecklers.sburrestdemo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SburRestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SburRestDemoApplication.class, args);
	}

	@Bean
	@ConfigurationProperties(prefix = "droid")
	Droid createDroid() {
		return new Droid();
	}
}

/**
 * RestApiDemoController 에서 샘플 데이터 생성 역할을 DataLoader 클래스로 역할 분리
 */
@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;

	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}
}

/**
 * Droid 속성 정상 동작 여부 확인 클래스
 */
@RestController
@RequestMapping("/droid")
class DroidController {
	private final Droid droid;

	public DroidController(Droid droid) {
		this.droid = droid;
	}

	@GetMapping
	Droid getDroid() {
		return droid;
	}
}

/**
 * '어플리케이션 속성' 설정 처리 위한 클래스
 */
@RestController
@RequestMapping("/greeting")
class GreetingController {

	// @ConfigurationProperties 활용하는 Greeting 빈에 대한 멤버 변수 선언
	private final Greeting greeting;

	// 생성자 주입
	public GreetingController(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping
	String getGreeting() {
		return greeting.getName();
	}

	@GetMapping("/coffee")
	String getNameAndCoffee() {
		return greeting.getCoffee();
	}
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {

	// CoffeeRepository 주입
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}

	// 단일 아이템 조회
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeebyId(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}

	// PUT 요청 - 기존 리소스 업데이트에 사용
	// 특정 식별자로 커피 검색하고 찾으면 업데이트, 없으면 리소스 생성
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {

		return (coffeeRepository.existsById(id))
				? new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED);

	}

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}

class Droid {
	private String id, description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

/* @ConfigurationProperties 활용 */
@ConfigurationProperties(prefix = "greeting")
class Greeting {
	private String name;
	private String coffee;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}
}


// DB 사용을 위한 Repository 인터페이스 상속할 인터페이스 정의
interface CoffeeRepository extends CrudRepository<Coffee, String> {}

@Entity
class Coffee {
	@Id
	private String id;		// 기본 생성자(no-argument constructor) 사용을 위해서는 모든 멤버 변수가 final이 아닌 변경 가능으로 되어야 함
	private String name;

	// 기본 생성자 추가 - JPA를 사용해 DB에 데이터 생성하기 위함.
	public Coffee() {
	}

	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	// id 매개변수 미전달 시, 고유 식별자인 id를 기본 제공
	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// id에 대한 변경자(mutator) 메서드 추가 - JPA가 id에 값 할당하기 위함
	public void setId(String id) {
		this.id = id;
	}
}