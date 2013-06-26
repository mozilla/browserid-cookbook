class SessionsController < ApplicationController

  def create
    @user = User.authenticate_with_persona(params[:assertion])
    if @user['email']
      session[:email] = @user['email']
      redirect_to root_url
    else
      flash.now.alert = "Invalid email or something"
      redirect_to root_url
    end
  end

  def destroy
    session[:email] = nil
    redirect_to root_url, :notice => "Logged out!"
  end
end
