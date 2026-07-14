import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './App.css'
import Login from './Login'

type Offer = { id: number, title: string, description: string }

export default function App(){
  const [offers, setOffers] = useState<Offer[]>([])
  const [fontSize, setFontSize] = useState<number>(parseInt(localStorage.getItem('fontSize')||'16'))
  const [highContrast, setHighContrast] = useState<boolean>(localStorage.getItem('highContrast')==='true')
  const [authenticated, setAuthenticated] = useState<boolean>(!!localStorage.getItem('auth'))
  const [isAdmin, setIsAdmin] = useState<boolean>(false)

  useEffect(()=>{ fetchOffers() },[])
  useEffect(()=>{ document.documentElement.style.fontSize = fontSize + 'px'; localStorage.setItem('fontSize', String(fontSize)) },[fontSize])
  useEffect(()=>{ document.documentElement.dataset.contrast = highContrast ? 'high' : 'normal'; localStorage.setItem('highContrast', String(highContrast)) },[highContrast])

  useEffect(()=>{ if (authenticated) checkAdmin(); else setIsAdmin(false) },[authenticated])

  async function fetchOffers(){
    try{ const res = await axios.get('/api/offers'); setOffers(res.data) } catch(e){ console.error(e) }
  }

  async function checkAdmin(){
    try{
      await axios.get('/api/users')
      setIsAdmin(true)
    }catch(e){ setIsAdmin(false) }
  }

  async function createOffer(){
    const title = window.prompt('Titel des Angebots')
    if(!title) return
    const description = window.prompt('Beschreibung') || ''
    try{
      await axios.post('/api/offers', { title, description })
      fetchOffers()
    }catch(e){ alert('Fehler beim Erstellen (evtl. fehlende Rechte)') }
  }

  return (
    <div className="app">
      <a className="skip-link" href="#main">Zum Inhalt springen</a>
      <header>
        <h1>Serviceportal</h1>
        <div className="access-controls">
          <label>Schriftgröße: 
            <button onClick={()=>setFontSize(s=>Math.max(12,s-1))} aria-label="kleiner">A-</button>
            <span aria-live="polite"> {fontSize}px </span>
            <button onClick={()=>setFontSize(s=>Math.min(24,s+1))} aria-label="größer">A+</button>
          </label>
          <label>
            <input type="checkbox" checked={highContrast} onChange={e=>setHighContrast(e.target.checked)} />
            Kontrastmodus
          </label>
          <div style={{marginLeft: '1rem'}}>
            <Login onAuthChange={(v:boolean)=>setAuthenticated(v)} />
          </div>
        </div>
      </header>

      <main id="main">
        <section aria-labelledby="offers-title">
          <h2 id="offers-title">Angebote</h2>
          {isAdmin && <div>
            <strong>Admin Controls:</strong>
            <button onClick={createOffer}>Angebot erstellen</button>
          </div>}
          <ul>
            {offers.map(o => (
              <li key={o.id}>
                <h3>{o.title}</h3>
                <p>{o.description}</p>
                <RequestForm offerId={o.id} offerTitle={o.title} />
              </li>
            ))}
          </ul>
        </section>
      </main>

    </div>
  )
}

function RequestForm({offerId, offerTitle}:{offerId:number, offerTitle:string}){
  const [name,setName]=useState('')
  const [email,setEmail]=useState('')
  const [message,setMessage]=useState('')
  const [sent, setSent]=useState(false)

  async function submit(e:React.FormEvent){
    e.preventDefault()
    try{
      await axios.post('/api/requests', { requesterName: name, requesterEmail: email, message: `Anfrage zu ${offerTitle}: ${message}` })
      setSent(true)
    }catch(e){ console.error(e); alert('Fehler beim Absenden') }
  }

  if(sent) return <div role="status">Danke, Ihre Anfrage wurde abgeschickt.</div>

  return (
    <form onSubmit={submit} className="request-form">
      <h4>Anfrage zu: {offerTitle}</h4>
      <label>Ihr Name<input required value={name} onChange={e=>setName(e.target.value)} /></label>
      <label>Email<input required type="email" value={email} onChange={e=>setEmail(e.target.value)} /></label>
      <label>Nachricht<textarea required value={message} onChange={e=>setMessage(e.target.value)} /></label>
      <button type="submit">Absenden</button>
    </form>
  )
}
