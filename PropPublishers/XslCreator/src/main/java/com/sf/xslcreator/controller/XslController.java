package com.sf.xslcreator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Node;

import com.properties.receiver.annotation.PropertyScan;
import com.properties.receiver.annotation.SetProperty;
import com.sf.xslcreator.service.StorageService;
import com.sf.xslcreator.serviceimpl.StorageException;

import net.sf.json.JSONObject;

@RestController
@PropertyScan
public class XslController {

	@SetProperty(property = "/editUserMgmt")
	private String productName;

	@Autowired
	private StorageService storageService;
	
	@RequestMapping("/transform")
	@ResponseBody
	public String login(@RequestParam("xmlString") String xmlString, @RequestParam("xslString") String xslString) {
		String result = "No Transformation";
		try {
			StringReader xmlReader = new StringReader(xmlString);
			StringReader xslReader = new StringReader(xslString);
			StringWriter writer = new StringWriter();
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(xslReader));
			transformer.transform(new StreamSource(xmlReader), new StreamResult(writer));

			result = writer.toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping("/fillxml")
	@ResponseBody
	public JSONObject fillXml(@RequestParam("inputJson") String inputJson) throws FileNotFoundException, JAXBException {
		String result = "No xml generated";
		JSONObject json = JSONObject.fromObject(productName);
		System.out.println(json.get("functionCode"));
		return json;
	}

	public String generateXML(String driverStr, String schemaStr) throws FileNotFoundException, JAXBException {

		StringReader schema = new StringReader(schemaStr);

		DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD((Node) schema, null, null,
				null);

		FileInputStream xmlInputStream = new FileInputStream("src/webapp/xmlfiles/sample1.xml");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		DynamicEntity customer = (DynamicEntity) unmarshaller.unmarshal(xmlInputStream);

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(customer, System.out);

		return driverStr;
	}

//	@RequestMapping(value = "/generateClassesold", method = { RequestMethod.POST })
//	public String welcome(Map<String, Object> model, @RequestParam("schemaFile") File schemaFile) throws IOException {
//		model.put("time", new Date());
//		model.put("message", this.productName);
//
//		return "";
//	}

	
	
	@PostMapping("/generateClasses")
    public String handleFileUpload(@RequestParam("schemaFile") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws StorageException {

        String fileContent = storageService.readFile(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return fileContent;
    }


}
