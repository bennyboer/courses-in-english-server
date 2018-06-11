package edu.hm.cs.cieserver.user;

import edu.hm.cs.cieserver.course.Course;
import edu.hm.cs.cieserver.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller handling user purposes.
 */
@RestController
@RequestMapping({"/api/users"})
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private CourseRepository courseRepository;

	@GetMapping(path = "/selected-course/{courseId}")
	public Set<User> findUsersWhoSelectedCourse(@PathVariable("courseId") Long courseId) {
		Optional<Course> course = courseRepository.findById(courseId);

		if (course.isPresent()) {
			return course.get().getSelectedBy();
		}

		return Collections.emptySet();
	}

	@GetMapping(path = "/favorized-course/{courseId}")
	public Set<User> findUsersWhoFavorizedCourse(@PathVariable("courseId") Long courseId) {
		Optional<Course> course = courseRepository.findById(courseId);

		if (course.isPresent()) {
			return course.get().getFavorizedBy();
		}

		return Collections.emptySet();
	}

	@GetMapping(path = "/current")
	public User currentUser(Principal principal) {
		if (principal != null) {
			return (User) userDetailsService.loadUserByUsername(principal.getName());
		}

		return null;
	}

	@PostMapping
	public User create(@RequestBody User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			return null;
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	@GetMapping(path = {"/{id}"})
	public User findOne(@PathVariable("id") Long id) {
		return userRepository.findById(id).get();
	}

	@PutMapping
	public ResponseEntity<User> update(@RequestBody User user, Principal principal) {
		User existing = findOne(user.getId());
		User userRequestingUpdate = (User) userDetailsService.loadUserByUsername(principal.getName());

		if (existing != null) {
			if (user.getIsAdministrator() && !userRequestingUpdate.getId().equals(existing.getId())) {
				// Admins can only be updated by themselves, not by other admins.
				return new ResponseEntity<>(new User(), HttpStatus.FORBIDDEN);
			}

			existing.setFirstName(user.getFirstName());
			existing.setLastName(user.getLastName());
			existing.setEmail(user.getEmail());
			existing.setIsAdministrator(user.getIsAdministrator());

			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				existing.setPassword(passwordEncoder.encode(user.getPassword()));
			}

			return new ResponseEntity<>(userRepository.save(existing), HttpStatus.OK);
		}

		return new ResponseEntity<>(new User(), HttpStatus.FORBIDDEN);
	}

	@DeleteMapping(path = {"/{id}"})
	public ResponseEntity<?> delete(@PathVariable("id") Long id, Principal principal) {
		HttpStatus status = HttpStatus.OK;

		if (principal != null) {
			User userRequestingDeletion = (User) userDetailsService.loadUserByUsername(principal.getName());

			Optional<User> userToDelete = userRepository.findById(id);

			if (userToDelete.isPresent()) {
				User user = userToDelete.get();

				if (user.getIsAdministrator()) {
					// Users who are admins can only be deleted by themselves and not by other admins.
					if (user.getId().equals(userRequestingDeletion.getId())) {
						userRepository.delete(user);
					} else {
						status = HttpStatus.FORBIDDEN;
					}
				} else {
					userRepository.delete(user);
				}
			}
		}

		return new ResponseEntity<>(null, status);
	}

	@GetMapping
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
