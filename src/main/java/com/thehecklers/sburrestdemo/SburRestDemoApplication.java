package com.thehecklers.sburrestdemo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class SburRestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SburRestDemoApplication.class, args);
	}

}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	private List<Coffee> coffees = new ArrayList<>();

	public RestApiDemoController() {
		coffees.addAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}

	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffees;
	}

	// 단일 아이템 조회
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeebyId(@PathVariable String id) {
		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c);
			}
		}

		return Optional.empty();    // 해당하는 항목이 없으면 비어있는 값 반환
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		coffees.add(coffee);
		return coffee;
	}

	// PUT 요청 - 기존 리소스 업데이트에 사용
	// 특정 식별자로 커피 검색하고 찾으면 업데이트, 없으면 리소스 생성
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		int coffeeIndex = -1;

		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}

		return (coffeeIndex == -1) ?
				new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) :
				new ResponseEntity<>(coffee, HttpStatus.OK);

	}

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffees.removeIf(c -> c.getId().equals(id));	// 근데 c 는 어디서 정의해서 가져온거지?? -_-;;
	}
}

@Entity
class Coffee {
	@Id
	private String id;		// 기본 생성자(no-argument constructor) 사용을 위해서는 모든 멤버 변수가 final이 아닌 변경 가능으로 되어야 함
	private String name;

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

	// id에 대한 변경자(mutator) 메서드 추가

	public void setId(String id) {
		this.id = id;
	}
}