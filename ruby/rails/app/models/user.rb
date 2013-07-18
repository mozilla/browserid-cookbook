require 'rest-client'
require 'json'

class User < ActiveRecord::Base
  attr_accessible :name, :email

  def self.authenticate_with_persona(assertion)
    server = 'https://verifier.login.persona.org/verify'
    assertion_params = {
      assertion: assertion,
      audience: 'http://localhost:3000'
    }
    request = RestClient::Resource.new(server, verify_ssl: true).post(assertion_params)
    response = JSON.parse(request)

    if response['status'] == 'okay'
      found_user = User.find_by_email(response['email']) 
      if found_user and found_user['email']
        puts 'Existing User'
      else
        puts 'Creating User'
        User.create(name: response['email'], email: response['email'])
      end

      return response
    else
      return {status: 'error'}.to_json
    end
  end
end
