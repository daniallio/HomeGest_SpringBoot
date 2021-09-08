package com.daniallio.webapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daniallio.webapp.entities.Oggetti;
import com.daniallio.webapp.entities.OggettiDTO;
import com.daniallio.webapp.entities.Stanza;
import com.daniallio.webapp.entities.Tipi;
import com.daniallio.webapp.exceptions.OggettoExistException;
import com.daniallio.webapp.exceptions.OggettoNotFoundException;
import com.daniallio.webapp.exceptions.StanzaNotFoundException;
import com.daniallio.webapp.exceptions.TipoNotFoundException;
import com.daniallio.webapp.services.OggettoService;
import com.daniallio.webapp.services.StanzaService;
import com.daniallio.webapp.services.TipiService;


@Controller
@RequestMapping("api/oggetto")
public class OggettoController {

	
	private static final Logger logger = LoggerFactory.getLogger(OggettoController.class);
	
	
	@Autowired
	OggettoService serviceOggetto;
	
	@Autowired
	StanzaService serviceStanza;
	
	@Autowired
	TipiService serviceTipo;
	
	
	//ritorno tutti gli oggetti
	@GetMapping(value = "/all", produces = "application/json")
	public ResponseEntity<List<OggettiDTO>> selAllOggetti (){
		
		List<Oggetti> stanze = serviceOggetto.sellAllOggetti();
		
		
		//converto l'elenco in DTO
		List<OggettiDTO> stanzeDTO = new ArrayList<OggettiDTO>();
				
		stanze.forEach(s -> stanzeDTO.add(s.convertToDTO()));
		
		return new ResponseEntity<List<OggettiDTO>>(stanzeDTO,HttpStatus.OK);
		
	}
	
	//inserisco un nuovo oggetto	
	@PostMapping(value="/inserisci", produces = "application/json")
	public ResponseEntity<OggettiDTO> insOggetto (@RequestBody OggettiDTO oggDTO) throws OggettoExistException,TipoNotFoundException,StanzaNotFoundException{
		
		logger.info("********Meedoto insOggetto. Stanza con ID " + oggDTO.getCodice());
		
		Optional <Tipi> tipoOpt;
		Optional <Stanza> stanzaOpt;
		
		
		//verifico che non sia già presente a sistema
		Optional <Oggetti> optOggetto = serviceOggetto.selOggettoById(oggDTO.getCodice());
		
		if(optOggetto.isPresent()) { //in caso esista eccezione
			
			throw new OggettoExistException("L'oggetto con codice " + oggDTO.getCodice() + " è già esistente");
			
		} //verifico che esistano la Stanza e il tipo
			
			stanzaOpt = serviceStanza.selStanzaById(oggDTO.getStanza());
			
			if(!stanzaOpt.isPresent()) {//verifico stanza
				
				throw new StanzaNotFoundException("Codice stanza non corretto");
				
			}else { //verifico tipo
				
				tipoOpt = serviceTipo.selTipoById(oggDTO.getTipo());
				if(!tipoOpt.isPresent()) {
					throw new TipoNotFoundException("Codice tipo non corretto");
				}
				
				
			}
				
			Oggetti oggetto = new Oggetti();			
			oggetto.setAttivo(oggDTO.isAttivo());
			oggetto.setCodice(oggDTO.getCodice());
			oggetto.setDataAcquisto(oggDTO.getDataAcquisto());
			oggetto.setDescrizione(oggDTO.getDescrizione());
			oggetto.setNote(oggDTO.getNote());
			oggetto.setStanza(stanzaOpt.get());
			oggetto.setTipo(tipoOpt.get());
			oggetto.setValore(oggDTO.getValore());

		
			serviceOggetto.insOggetto(oggetto);
		
			return new ResponseEntity<OggettiDTO>(oggDTO,HttpStatus.OK);
		
		
	}
	
	
	
	//aggiorno oggetto
	@PutMapping(value ="/aggiorna", produces = "application/json")
	public ResponseEntity<OggettiDTO> updOggetto (@RequestBody OggettiDTO oggDTO) throws OggettoNotFoundException, StanzaNotFoundException, TipoNotFoundException{
		
		
		logger.info("********Medoto updOggetto. Oggetto con ID " + oggDTO.getCodice());
		
		Optional <Tipi> tipoOpt;
		Optional <Stanza> stanzaOpt;
		//verifico l'esistenza dell'oggetto
		Optional<Oggetti> oggOpt = serviceOggetto.selOggettoById(oggDTO.getCodice());
		
		if(!oggOpt.isPresent()) {
			throw new OggettoNotFoundException("L'oggetto " +  oggDTO.getCodice() + " non è esistente");
		}
		
		
		stanzaOpt = serviceStanza.selStanzaById(oggDTO.getStanza());
		
		if(!stanzaOpt.isPresent()) {//verifico stanza
			
			throw new StanzaNotFoundException("Codice stanza non corretto");
			
		}else { //verifico tipo
			
			tipoOpt = serviceTipo.selTipoById(oggDTO.getTipo());
			if(!tipoOpt.isPresent()) {
				throw new TipoNotFoundException("Codice tipo non corretto");
			}
			
			
		}
		
		
		
		
		
		Oggetti oggetto = new Oggetti();			
		oggetto.setAttivo(oggDTO.isAttivo());
		oggetto.setCodice(oggDTO.getCodice());
		oggetto.setDataAcquisto(oggDTO.getDataAcquisto());
		oggetto.setDescrizione(oggDTO.getDescrizione());
		oggetto.setNote(oggDTO.getNote());
		oggetto.setStanza(stanzaOpt.get());
		oggetto.setTipo(tipoOpt.get());
		oggetto.setValore(oggDTO.getValore());
		
		serviceOggetto.insOggetto(oggetto);
		
		return new ResponseEntity<OggettiDTO>(oggDTO,HttpStatus.OK);
		
		
	}
	
}
