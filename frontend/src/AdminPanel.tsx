import React, { useState } from 'react'
import RequestsAdmin from './RequestsAdmin'
import UsersAdmin from './UsersAdmin'

export default function AdminPanel(){
  const [tab, setTab] = useState<'requests'|'users'>('requests')

  return (
    <div className="admin-panel" role="region" aria-label="Administration">
      <h2>Administration</h2>
      <nav aria-label="Admin-Navigation">
        <button onClick={()=>setTab('requests')} aria-pressed={tab==='requests'}>Anfragen</button>
        <button onClick={()=>setTab('users')} aria-pressed={tab==='users'}>Benutzer</button>
      </nav>
      <section style={{marginTop: '1rem'}}>
        {tab==='requests' ? <RequestsAdmin /> : <UsersAdmin />}
      </section>
    </div>
  )
}
