package com.example

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.stream.Stream
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@SpringBootApplication
open class SpringBootKotlinDemoApplication(val personRepository: PersonRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {

        personRepository.deleteAll();

        Stream.of(
                "Foo, Bar", "Foo1, Bar1", "Foo2, Bar2", "Foo3, Bar3",
                "Test1, Demo", "Test2, Demo", "Test3, Demo", "Test4, Demo"
        )
                .map { fn -> fn.split(",") }
                .forEach { tpl -> personRepository.save(Person(tpl[0].trim(), tpl[1].trim())) };

        personRepository.findAll().forEach { person -> println(person) }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(SpringBootKotlinDemoApplication::class.java, *args)
}

@Entity
@Table(name = "person")
data class Person(var firstName: String? = null,
                          var lastName: String? = null,
                          @Id @GeneratedValue(strategy = javax.persistence.GenerationType.SEQUENCE) var id: Integer? = null)

@RepositoryRestResource(collectionResourceRel = "person", path = "person")
interface PersonRepository : PagingAndSortingRepository <Person, Integer> {

    fun findPersonByFirstName(@Param("firstName") firstName: String): List<Person>

    @Query("from Person T where T.lastName = :surname")
    fun findPersonByFamily(@Param("surname") surname : String): List<Person>

}