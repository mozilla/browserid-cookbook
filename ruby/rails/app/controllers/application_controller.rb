class ApplicationController < ActionController::Base
  protect_from_forgery

  helper_method :current_user, :authenticate_user

  private

  def current_user
    @current_user ||= User.find_by_email(session[:email]) if session[:email]
  end
end
