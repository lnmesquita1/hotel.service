package com.senior.hotel.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senior.hotel.entity.Hospede;
import com.senior.hotel.entity.HospedeValor;
import com.senior.hotel.repository.HospedeRepository;
import com.senior.hotel.repository.HospedeValorRepository;
import com.senior.hotel.response.Response;

@RestController
@RequestMapping("/api/hospede")
@CrossOrigin(origins = "*")
public class HospedeController {
	
	@Autowired
	private HospedeRepository hospedeRepository;
	
	@Autowired
	private HospedeValorRepository hospedeValorRepository;

	@PostMapping()
    public ResponseEntity<Response<Hospede>> createHospede(@RequestBody Hospede hospede) {
		Response<Hospede> response = new Response<Hospede>();
		try {
			Hospede persisted = hospedeRepository.save(hospede);
			response.setData(persisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
    }
	
	@GetMapping(value="left")
	public ResponseEntity<Response<List<Map<String, Object>>>> findGuestsWithCheckInAndLeftHotel() {
		Response<List<Map<String, Object>>> response = new Response<List<Map<String, Object>>>();
		List<Hospede> guests = hospedeRepository.findGuestsWithCheckInAndLeftHotel(LocalDate.now());
		
		List<Map<String, Object>> retorno = new ArrayList<>();
		guests.stream().forEach(guest -> {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("hospede", guest);
			List<HospedeValor> hospedeValor = hospedeValorRepository.findGuestValues(guest.getId());
			if (!hospedeValor.isEmpty()) {
				Double total = hospedeValor.stream().mapToDouble(i -> i.getValor()).sum();
				Double lastAccommodation = hospedeValor.get(hospedeValor.size()-1).getValor();
				item.put("total", total);
				item.put("lastAccomodation", lastAccommodation);
			}
			
			retorno.add(item);
		});
		
		response.setData(retorno);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value="hosted")
	public ResponseEntity<Response<List<Hospede>>> findGuestsHosted() {
		Response<List<Hospede>> response = new Response<List<Hospede>>();
		List<Hospede> guests = hospedeRepository.findGuestsHosted(LocalDate.now());
		response.setData(guests);
		return ResponseEntity.ok(response);
	}
}