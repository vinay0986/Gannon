package com.vinni.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
/*import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;*/
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.vinni.entity.AuctionTransaction;
import com.vinni.entity.AuctionTransactionHistory;
import com.vinni.entity.DonationTransaction;
import com.vinni.entity.ProductImage;
import com.vinni.entity.Users;

@Path("/auctionOrDonationService")
public class AuctionOrDonationService {

	@Path("/save")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response save(final auctionOrDonationServiceRequest input) {
		try {
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(message);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@POST
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
				InputStream inputStream = null;
				try {
					inputStream = inputPart.getBody(InputStream.class, null);
					byte[] bytes = IOUtils.toByteArray(inputStream);
					saveFile(bytes, fileName);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					inputStream.close();
				}
			}
			SuccessMessagePojo success = new SuccessMessagePojo();
			success.setStatusCode(Response.Status.OK.getStatusCode());
			success.setStatus("Success");
			success.setMessage(fileNames);
			return Response.ok(success).build();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessagePojo error = new ErrorMessagePojo();
			error.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			error.setStatus("Failure");
			error.setMessage("Unable Process your request");
			return Response.ok(error).build();
		}
	}

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
		System.out.println("System.getProperty(\"catalina.home\")============" + System.getProperty("catalina.home"));
		String imageFileDir = System.getProperty("catalina.home") + File.separator + "images" + File.separator;
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

	@Path("/list")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response list(final AuctionOrDonationListServiceReq input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Users user = em.find(Users.class, input.getUserId());
			List<AuctionDOnationListServiceRes> results = new ArrayList<AuctionDOnationListServiceRes>(0);
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				List<AuctionTransaction> list = em
						.createNativeQuery(
								"select * from auction_transaction where auction_created_by=:usr "
										+ " order by auction_transaction_id desc LIMIT :lim OFFSET :offset",
								AuctionTransaction.class)
						.setParameter("usr", input.getUserId()).setParameter("lim", input.getLimit())
						.setParameter("offset", input.getOffset()).getResultList();

				List<Integer> ids = list.stream().map(it -> it.getAuctionTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where auctionTransaction is not null "
									+ "and auctionTransaction.auctionTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getAuctionTransaction().getAuctionTransactionId()))
							imgMap.put(img.getAuctionTransaction().getAuctionTransactionId(), img.getImagePath());
					}

				}

				for (AuctionTransaction at : list) {
					AuctionDOnationListServiceRes res = new AuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setAuctionId(at.getAuctionTransactionId());
					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getAuctionTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getAuctionTransactionId()));
						}
					}
					results.add(res);
				}
			} else {
				List<DonationTransaction> list = em
						.createNativeQuery(
								"select * from donation_transaction where donation_created_by=:usr "
										+ " order by donation_transaction_id desc LIMIT :lim OFFSET :offset",
								DonationTransaction.class)
						.setParameter("usr", input.getUserId()).setParameter("lim", input.getLimit())
						.setParameter("offset", input.getOffset()).getResultList();

				List<Integer> ids = list.stream().map(it -> it.getDonationTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where donationTransaction is not null "
									+ "and donationTransaction.donationTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getAuctionTransaction().getAuctionTransactionId()))
							imgMap.put(img.getAuctionTransaction().getAuctionTransactionId(), img.getImagePath());
					}
				}
				for (DonationTransaction at : list) {
					AuctionDOnationListServiceRes res = new AuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setDonationId(at.getDonationTransactionId());

					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getDonationTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getDonationTransactionId()));
						}
					}
					results.add(res);
				}

			}
			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(results);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@Path("/history")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response history(final AuctionDonationDetailsRequest input) {
		try {
			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<AuctionDetailsHistoryRes> results = new ArrayList<>(0);
			List<AuctionTransactionHistory> history = em
					.createQuery("from AuctionTransactionHistory where auctionTransaction.auctionTransactionId=:id "
							+ "order by auctionTransactionHistoryId desc")
					.setParameter("id", input.getAuctionId()).getResultList();
			for (AuctionTransactionHistory his : history) {
				AuctionDetailsHistoryRes res = new AuctionDetailsHistoryRes();
				res.setAuctionUser(his.getAictionUser().getFirstName() + " " + his.getAictionUser().getLastName());
				res.setAuctionAmount(his.getAuctionAmount());
				res.setAuctionDate(sdf.format(his.getAuctionPriceChangeDate()));
				results.add(res);
			}
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(results);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@Path("/details")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response details(final AuctionDonationDetailsRequest input) {
		try {
			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			AuctionDonationDetailsResponse res = new AuctionDonationDetailsResponse();
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				AuctionTransaction at = em.find(AuctionTransaction.class, input.getAuctionId());

				List<ProductImage> imgList = em
						.createQuery("from ProductImage where auctionTransaction is not null "
								+ "and auctionTransaction.auctionTransactionId =:ids order by productImageId asc")
						.setParameter("ids", input.getAuctionId()).getResultList();

				res.setProductName(at.getProductName());
				res.setAuctionAmount(at.getAuctionAmount());
				res.setAuctionCloseDate(sdf.format(at.getAuctionCloseDate()));
				res.setAuctionStatus(at.getAuctionStatus());
				res.setInitialAmount(at.getInitialAuctionAmount());
				res.setProductDescription(at.getProductDescription());
				List<String> images = new ArrayList<>(0);
				for (ProductImage img : imgList) {
					images.add("img/" + img.getImagePath());
				}
				res.setImagesList(images);
			} else {
				DonationTransaction at = em.find(DonationTransaction.class, input.getDonationId());

				List<ProductImage> imgList = em
						.createQuery("from ProductImage where donationTransaction is not null "
								+ "and donationTransaction.donationTransactionId =:ids order by productImageId asc")
						.setParameter("ids", input.getDonationId()).getResultList();

				res.setProductName(at.getProductName());
				res.setAuctionCloseDate(sdf.format(at.getDonationCloseDate()));
				res.setAuctionStatus(at.getDonationProductStatus());
				res.setProductDescription(at.getProductDescription());
				List<String> images = new ArrayList<>(0);
				for (ProductImage img : imgList) {
					images.add("img/" + img.getImagePath());
				}
				res.setImagesList(images);
			}
			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(res);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (

		Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@Path("/update")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response update(final AuctionDonationUpdateReq input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				AuctionTransaction at = em.find(AuctionTransaction.class, input.getAuctionId());
				at.setProductName(input.getProductName());
				at.setProductDescription(input.getProductDescription());
				at.setAuctionCloseDate(form.parse(input.getAuctionCloseDate()));
				at.setAuctionStatus(input.getAuctionStatus());
				em.merge(at);
				if (!input.getImagesList().isEmpty()) {
					for (String s : input.getImagesList()) {
						ProductImage img = new ProductImage();
						img.setAuctionTransaction(at);
						img.setDonationTransaction(null);
						img.setImagePath(s);
						img.setUploadedDate(new Date());
						em.persist(img);
					}
				}

			} else {
				DonationTransaction at = em.find(DonationTransaction.class, input.getDonationId());
				at.setProductName(input.getProductName());
				at.setProductDescription(input.getProductDescription());
				at.setDonationCloseDate(form.parse(input.getAuctionCloseDate()));
				at.setDonationProductStatus(input.getAuctionStatus());
				em.merge(at);
				if (!input.getImagesList().isEmpty()) {
					for (String s : input.getImagesList()) {
						ProductImage img = new ProductImage();
						img.setAuctionTransaction(null);
						img.setDonationTransaction(at);
						img.setImagePath(s);
						img.setUploadedDate(new Date());
						em.persist(img);
					}
				}

			}
			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage("Successfully Updated the details");
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (

		Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setMessage("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}
}

class AuctionDetailsHistoryRes {
	private String auctionUser;
	private String auctionDate;
	private float auctionAmount;

	public String getAuctionUser() {
		return auctionUser;
	}

	public void setAuctionUser(String auctionUser) {
		this.auctionUser = auctionUser;
	}

	public String getAuctionDate() {
		return auctionDate;
	}

	public void setAuctionDate(String auctionDate) {
		this.auctionDate = auctionDate;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

}

class AuctionDonationUpdateReq {
	private String productName;
	private String productDescription;
	private String auctionCloseDate;
	private String auctionStatus;
	private List<String> imagesList = new ArrayList<String>(0);
	private int auctionId;
	private int donationId;
	private String auctionOrDonation;

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public int getDonationId() {
		return donationId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public String getAuctionOrDonation() {
		return auctionOrDonation;
	}

	public void setAuctionOrDonation(String auctionOrDonation) {
		this.auctionOrDonation = auctionOrDonation;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getAuctionCloseDate() {
		return auctionCloseDate;
	}

	public void setAuctionCloseDate(String auctionCloseDate) {
		this.auctionCloseDate = auctionCloseDate;
	}

	public String getAuctionStatus() {
		return auctionStatus;
	}

	public void setAuctionStatus(String auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	public List<String> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<String> imagesList) {
		this.imagesList = imagesList;
	}

}

class AuctionDonationDetailsResponse {
	private String productName;
	private String productDescription;
	private String auctionCloseDate;
	private String auctionStatus;
	private float auctionAmount;
	private float initialAmount;
	private List<String> imagesList = new ArrayList<>(0);

	public float getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(float initialAmount) {
		this.initialAmount = initialAmount;
	}

	public List<String> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<String> imagesList) {
		this.imagesList = imagesList;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getAuctionCloseDate() {
		return auctionCloseDate;
	}

	public void setAuctionCloseDate(String auctionCloseDate) {
		this.auctionCloseDate = auctionCloseDate;
	}

	public String getAuctionStatus() {
		return auctionStatus;
	}

	public void setAuctionStatus(String auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

}

class AuctionDonationDetailsRequest {
	private int auctionId;
	private int donationId;
	private String auctionOrDonation;

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public int getDonationId() {
		return donationId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public String getAuctionOrDonation() {
		return auctionOrDonation;
	}

	public void setAuctionOrDonation(String auctionOrDonation) {
		this.auctionOrDonation = auctionOrDonation;
	}

}

class AuctionDOnationListServiceRes {
	private String productName;
	private String imageUrl;
	private int totalCount;
	private int auctionId;
	private int donationId;

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public int getDonationId() {
		return donationId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}

class AuctionOrDonationListServiceReq {
	private int userId;
	private String auctionOrDonation;
	private int offset;
	private int limit;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAuctionOrDonation() {
		return auctionOrDonation;
	}

	public void setAuctionOrDonation(String auctionOrDonation) {
		this.auctionOrDonation = auctionOrDonation;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
