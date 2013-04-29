require "rubygems"
require "sinatra"
require 'json'
require 'rest-client'

enable :sessions

helpers do
  
  def login?
    !session[:email].nil?
  end
  
end

get "/" do
  erb :index
end

post "/auth/login" do
  # check assertion with a request to the verifier
  response = nil
  if params[:assertion]
    restclient_url = "https://verifier.login.persona.org/verify"
    restclient_params = {
      :assertion => params["assertion"],
      :audience  => "http://localhost:#{request.port}", # use your website's URL here.
    }
    response = JSON.parse(RestClient::Resource.new(restclient_url, :verify_ssl => true).post(restclient_params))
  end

  # create a session if assertion is valid
  if response["status"] == "okay"
    session[:email] = response["email"]
    response.to_json
  else
    {:status => "error"}.to_json
  end
end

get "/auth/logout" do
   session[:email] = nil
   redirect "/"
end
