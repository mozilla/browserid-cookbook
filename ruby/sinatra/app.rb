require "rubygems"
require "haml"
require "sinatra"
require 'json'
require 'rest-client'
require 'digest/md5'

enable :sessions

helpers do
  
  def login?
    return !session[:email].nil?
  end
  
  def getGravatarURL
    return "http://www.gravatar.com/avatar/#{Digest::MD5.hexdigest(session[:email].strip.downcase)}"
  end
   
end

get "/" do
  haml :index
end

post "/auth/login" do
  # check assertion with a request to the verifier
  response = nil
  if params[:assertion]
    restclient_url = "https://verifier.login.persona.org/verify"
    restclient_params = {
      :assertion => params["assertion"],
      :audience  => "#{request.host}:#{request.port}",
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
