App::Application.routes.draw do

  root to: 'root#index'
  post 'login', to: 'sessions#create'
  post 'logout', to: 'sessions#destroy'
end
