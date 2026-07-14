import React, { useState } from 'react'
import axios from 'axios'

export default function Login({ onAuthChange }:{ onAuthChange:(v:boolean)=>void }){
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('auth'))

  async function login(e?:React.FormEvent){
    if (e) e.preventDefault()
    try{
      const token = btoa(`${username}:${password}`)
      // set header for test call
      axios.defaults.headers.common['Authorization'] = `Basic ${token}`
      // test by calling a protected endpoint; if 200 -> ok
      await axios.get('/api/users')
      localStorage.setItem('auth', token)
      setLoggedIn(true)
      onAuthChange(true)
    }catch(err){
      // login failed; clear header
      delete axios.defaults.headers.common['Authorization']
      localStorage.removeItem('auth')
      setLoggedIn(false)
      onAuthChange(false)
      alert('Login fehlgeschlagen (Benutzername/Passwort prüfen)')
    }
  }

  function logout(){
    delete axios.defaults.headers.common['Authorization']
    localStorage.removeItem('auth')
    setLoggedIn(false)
    onAuthChange(false)
  }

  if (loggedIn) return <div>
    <button onClick={logout}>Abmelden</button>
  </div>

  return (
    <form onSubmit={login} style={{display:'inline-flex', gap: '0.5rem', alignItems:'center'}}>
      <label style={{display:'none'}}>Benutzername<input value={username} onChange={e=>setUsername(e.target.value)} /></label>
      <label style={{display:'none'}}>Passwort<input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></label>
      <input aria-label="Benutzername" placeholder="Benutzer" value={username} onChange={e=>setUsername(e.target.value)} />
      <input aria-label="Passwort" placeholder="Passwort" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
      <button type="submit">Anmelden</button>
    </form>
  )
}
