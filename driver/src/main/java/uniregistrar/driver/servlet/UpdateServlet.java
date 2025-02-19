package uniregistrar.driver.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uniregistrar.request.UpdateRequest;
import uniregistrar.state.UpdateState;

public class UpdateServlet extends AbstractServlet implements Servlet {

	private static final long serialVersionUID = 8532462131637520098L;

	private static Logger log = LoggerFactory.getLogger(UpdateServlet.class);

	public UpdateServlet() {

		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// read request

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		UpdateRequest updateRequest = UpdateRequest.fromJson(request.getReader());

		if (log.isInfoEnabled()) log.info("Driver: Incoming update request: " + updateRequest);

		if (updateRequest == null) {

			sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, null, "Driver: No update request found.");
			return;
		}

		// invoke the driver

		UpdateState updateState;
		String updateStateString;

		try {

			updateState = InitServlet.getDriver().update(updateRequest);
			updateStateString = updateState == null ? null : updateState.toJson();
		} catch (Exception ex) {

			if (log.isWarnEnabled()) log.warn("Driver: Update problem for " + updateRequest + ": " + ex.getMessage(), ex);
			sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null, "Driver: Update problem: " + ex.getMessage());
			return;
		}

		if (log.isInfoEnabled()) log.info("Driver: Update state for " + updateRequest + ": " + updateStateString);

		// no update state?

		if (updateState == null) {

			sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null, "Driver: No update state.");
			return;
		}

		// write update state

		sendResponse(response, HttpServletResponse.SC_OK, UpdateState.MEDIA_TYPE, updateStateString);
	}
}
