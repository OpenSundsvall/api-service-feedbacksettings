package se.sundsvall.feedbacksettings.api.exception;

/**
 * RuntimeException wrapper over ServiceException.
 */
public class ServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 7318691319457314857L;

	public ServiceRuntimeException(ServiceException exception) {
		super(exception);
	}

	@Override
	public String getMessage() {
		return super.getCause().getMessage();
	}

	public ServiceException getTypedCause() {
		return (ServiceException) super.getCause();
	}
}
