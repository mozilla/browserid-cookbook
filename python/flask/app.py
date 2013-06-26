from flask import Flask, abort, redirect, render_template, request, session
import requests

app = Flask(__name__)
app.secret_key= ''

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/auth/login', methods=["POST"])
def login():
    if 'assertion' not in request.form:
        abort(400)

    assertion_info = {'assertion': request.form['assertion'],
                        'audience': 'localhost:5000' } # window.location.host
    resp = requests.post('https://verifier.login.persona.org/verify',
                        data=assertion_info, verify=True)

    if not resp.ok:
        abort(500)

    data = resp.json()

    if data['status'] == 'okay':
        session.update({'email': data['email']})
        return resp.content

@app.route('/auth/logout', methods=["POST"])
def logout():
    session.pop('email', None)
    return redirect('/')

if __name__ == "__main__":
    app.run(debug=True)
