module Helpers
  def login?
    !session[:email].nil?
  end
end
