import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('PriceHistory e2e test', () => {
  const priceHistoryPageUrl = '/price-history';
  const priceHistoryPageUrlPattern = new RegExp('/price-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const priceHistorySample = { date: '2022-03-28' };

  let priceHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/price-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/price-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/price-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (priceHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/price-histories/${priceHistory.id}`,
      }).then(() => {
        priceHistory = undefined;
      });
    }
  });

  it('PriceHistories menu should load PriceHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('price-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PriceHistory').should('exist');
    cy.url().should('match', priceHistoryPageUrlPattern);
  });

  describe('PriceHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(priceHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PriceHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/price-history/new$'));
        cy.getEntityCreateUpdateHeading('PriceHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', priceHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/price-histories',
          body: priceHistorySample,
        }).then(({ body }) => {
          priceHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/price-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/watcher/api/price-histories?page=0&size=20>; rel="last",<http://localhost/services/watcher/api/price-histories?page=0&size=20>; rel="first"',
              },
              body: [priceHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(priceHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PriceHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('priceHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', priceHistoryPageUrlPattern);
      });

      it('edit button click should load edit PriceHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PriceHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', priceHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of PriceHistory', () => {
        cy.intercept('GET', '/services/watcher/api/price-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('priceHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', priceHistoryPageUrlPattern);

        priceHistory = undefined;
      });
    });
  });

  describe('new PriceHistory page', () => {
    beforeEach(() => {
      cy.visit(`${priceHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PriceHistory');
    });

    it('should create an instance of PriceHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-03-28').should('have.value', '2022-03-28');

      cy.get(`[data-cy="open"]`).type('74301').should('have.value', '74301');

      cy.get(`[data-cy="high"]`).type('66392').should('have.value', '66392');

      cy.get(`[data-cy="low"]`).type('60857').should('have.value', '60857');

      cy.get(`[data-cy="close"]`).type('52809').should('have.value', '52809');

      cy.get(`[data-cy="adjClose"]`).type('57953').should('have.value', '57953');

      cy.get(`[data-cy="volume"]`).type('81563').should('have.value', '81563');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        priceHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', priceHistoryPageUrlPattern);
    });
  });
});
