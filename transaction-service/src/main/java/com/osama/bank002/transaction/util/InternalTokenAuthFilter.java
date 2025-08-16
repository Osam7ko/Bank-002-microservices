package com.osama.bank002.transaction.util;

//@Component
//public class InternalTokenAuthFilter extends OncePerRequestFilter {
//    @Value("${tx.internal.token}")
//    String expected;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//
//        // handle context path and any trailing stuff (no assumptions)
//        String path = req.getRequestURI();
//        if ("POST".equalsIgnoreCase(req.getMethod()) && path.startsWith(req.getContextPath() + "/api/transactions/log")) {
//            String token = req.getHeader("X-Internal-Token");
//            if (token != null && token.equals(expected)) {
//                var auth = new UsernamePasswordAuthenticationToken(
//                        "account-service", null, List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//        chain.doFilter(req, res);
//    }
//}