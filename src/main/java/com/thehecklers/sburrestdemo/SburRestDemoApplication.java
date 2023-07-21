package com.thehecklers.sburrestdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;
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

/* DB 접속을 위해 스프링 데이터에서 제공하는 저장소(Repository) 사용을 위한 상속 */
interface CoffeeRepository extends CrudRepository<Coffee, String> {}	// 저장할 객체 타입 Coffee 와 고유 ID 저장할 String 타입

@Entity
class Coffee {
	// Coffee 클래스 멤버변수 (id: 특정 커피 종류의 고유 식별값, name: 커피(종류)명)
	@Id
	private String id;
	private String name;

	/* JPA 사용해서 DB 데이터 생성 시, 반드시 '기본 생성자' 생성 필요! -> 모든 멤버 변수는 final이 아닌 '변경 가능'이어야 하며, setter 추가 */
	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	// Coffee 인스턴스 생성 시 id 매개변수 미입력하면 고유 식별자인 id값을 기본으로 제공
	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	// default constructor
	public Coffee() {}

	// getter, setter
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}
}


/*
   @RestController 실습
   @RestController = @Controller + @ResponseBody
 */
@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	/* Spring JPA Data Repository 기능 사용하기 위해 아래 코드 주석 */
//	private List<Coffee> coffees = new ArrayList<>();    // 여러 Coffee 객체 반환하기 위해 'Coffee 객체의 List 형태'로 정의
	private final CoffeeRepository coffeeRepository;
	/* 데이터 로딩은 별도 분리한 DataLoader에서 수행하고, 여기서는 coffeeRepository만 호출 */
	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
		/*
		this.coffeeRepository = coffeeRepository;

		this.coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
		*/
		/*
		// Spring JPA Data Repository 기능 구현 위한 주석 처리
		coffees.addAll(List.of(new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
		*/
	}

	//	@RequestMapping(value = "/coffees", method = RequestMethod.GET)	// getCoffees() 메서드가 GET 요청의 /coffees URL에만 응답하게 제한
	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}

	/* 특정 커피 조회용도/ 경로상의 {id}는 URI변수 / @PathVariable 어노테이션이 달린 id 매개변수를 통해 getCoffeeById 메서드에 전달됨 */
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		/*
		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c);
			}
		}

		return Optional.empty();
		*/
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		/*
		coffees.add(coffee);
		return coffee;
		*/
		return coffeeRepository.save(coffee);
	}

	/* PUT으로 업데이트 / 특정 식별자로 커피 검색 후 찾으면 업데이트, 없으면 리소스 생성 (postCoffee() 호출해서) */
	/* IETF 문서에서 PUT 메서드 응답 시 '상태 코드'가 필수이기 때문에 추가 수정 */
	@PutMapping("/{id}")
	// 상태 코드 지정 위한 주석 처리
//	Coffee putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		/*
		int coffeeIndex = -1;

		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}
		// 상태 코드 지정 위한 주석 처리
//		return (coffeeIndex == -1) ? postCoffee(coffee) : coffee;
		return (coffeeIndex == -1) ?
				new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) :
				new ResponseEntity<>(coffee, HttpStatus.OK);
		*/
		return (coffeeRepository.existsById(id))
				? new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED);
	}

	/* DELETE / @PathVariable로 전달받은 식별자 id를 받아서 목록에서 제거 */
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
//		coffees.removeIf(c -> c.getId().equals(id));
		coffeeRepository.deleteById(id);
	}
}

/* 데이터 로딩 작업을 위한 별도 클래스 생성 */
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