package com.vinni.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
/*import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;*/

import com.vinni.entity.AuctionTransaction;
import com.vinni.entity.DonationTransaction;
import com.vinni.entity.ProductImage;
import com.vinni.entity.Users;

@Path("/auctionOrDonationService")
public class AuctionOrDonationService {

	@Path("/save")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response save(final auctionOrDonationServiceRequest input) {
		try {
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			final Users reg = (Users) em.find(Users.class, (Object) input.getUserId());
			String message = null;
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				AuctionTransaction au = new AuctionTransaction();
				au.setAuctionAmount(input.getAuctionAmount());
				au.setAuctionCloseDate(form.parse(input.getCloseDate()));
				au.setAuctionCreatedBy(reg);
				au.setAuctionCreatedDate(new Date());
				au.setInitialAuctionAmount(input.getAuctionAmount());
				au.setProductDescription(input.getProductDescription());
				au.setProductName(input.getProductName());
				au.setAuctionStatus("OPEN");
				em.persist(au);
				if (!input.getImagesList().isEmpty()) {
					for (String s : input.getImagesList()) {
						ProductImage img = new ProductImage();
						img.setAuctionTransaction(au);
						img.setDonationTransaction(null);
						img.setImagePath(s);
						img.setUploadedDate(new Date());
						em.persist(img);
					}
				}
				message = "Successfully saved the auction details";
			} else {
				DonationTransaction dt = new DonationTransaction();
				dt.setDonationCloseDate(form.parse(input.getCloseDate()));
				dt.setDonationCreatedBy(reg);
				dt.setDonationCreatedDate(new Date());
				dt.setDonationProductStatus("OPEN");
				dt.setProductDescription(input.getProductDescription());
				dt.setProductName(input.getProductName());
				em.persist(dt);
				if (!input.getImagesList().isEmpty()) {
					for (String s : input.getImagesList()) {
						ProductImage img = new ProductImage();
						img.setAuctionTransaction(null);
						img.setDonationTransaction(dt);
						img.setImagePath(s);
						img.setUploadedDate(new Date());
						em.persist(img);
					}
				}
				message = "Successfully saved the donation details";
			}
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(message);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	/*@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFiles2(MultipartFormDataInput input) {
		try {
			Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
			List<InputPart> inputParts = uploadForm.get("files");
			List<String> fileNames = new ArrayList<>();
			for (InputPart inputPart : inputParts) {
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				String fileName = getFileName(header);
				fileName = fileName.replace(fileName.substring(0, fileName.indexOf(".")),
						String.valueOf(System.currentTimeMillis()));
				fileNames.add(fileName);
				InputStream inputStream = inputPart.getBody(InputStream.class, null);
				byte[] bytes = IOUtils.toByteArray(inputStream);
				saveFile(bytes, fileName);
			}
			SuccessMessagePojo success = new SuccessMessagePojo();
			success.setStatusCode(Response.Status.OK.getStatusCode());
			success.setStatus("Success");
			success.setMessage(fileNames);
			return Response.ok(success).build();
		} catch (Exception e) {
			ErrorMessagePojo error = new ErrorMessagePojo();
			error.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			error.setStatus("Failure");
			error.setMessage("Unable Process your request");
			return Response.ok(error).build();
		}
	}*/

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {

			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	private void saveFile(byte[] file, String name) {
		FileOutputStream fos = null;
		String imageFileDir = System.getProperty("jboss.home.dir") + File.separator + "images" + File.separator;
		File f = new File(imageFileDir);
		f.mkdirs();
		imageFileDir = f.getPath() + File.separator + name;

		try {
			fos = new FileOutputStream(imageFileDir);
			fos.write(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
					fos.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

