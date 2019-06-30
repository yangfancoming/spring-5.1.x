

package org.springframework.test.web.servlet.samples.context;

import org.springframework.test.web.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {

	private final PersonDao personDao;


	PersonController(PersonDao personDao) {
		this.personDao = personDao;
	}

	@GetMapping("/{id}")
	public Person getPerson(@PathVariable long id) {
		return this.personDao.getPerson(id);
	}

}
