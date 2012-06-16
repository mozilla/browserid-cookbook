require "rubygems"
require "haml"
require "sinatra"
require 'nestful'
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
  if params[:assertion]
    data = Nestful.post "https://browserid.org/verify", :format => :json, :params => { :assertion => "#{params[:assertion]}", :audience => "http://#{request.host}:#{request.port}" }
    if data["status"] == "okay"
      session[:email] = data["email"]
      return data.to_json
    end
  end
  return {:status => "error"}.to_json
end

get "/auth/logout" do
   session[:email] = nil
   redirect "/"
end
        